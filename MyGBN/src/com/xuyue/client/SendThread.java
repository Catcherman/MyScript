/**
 * 
 */
package com.xuyue.client;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import javax.swing.Timer;
import com.xuyue.client.ClientMainWindow;
import com.xuyue.client.SendThread.SendDataThread;
import com.xuyue.client.SendThread.Timeout;
import com.xuyue.common.C;
import com.xuyue.common.Md5;
import com.xuyue.common.Packet;

/**
 * @author xuyue
 *
 */
public class SendThread extends Thread {
	private ClientMainWindow mw;
	public static final int PACKET_AMOUNT = 15;//Ҫ���͵İ����ܸ���
	public String allData[];
	private int nextSendNum = 0;//�����͵���һ�����ݵ����
	private ArrayList<Packet> sndpkt;
	private int N = 5;//���ڴ�С
	private int base;//������ȷ���յ��޴�ACK�İ�������ֵ
	private int nextSeqNum;//��һ����ȷ�ϵİ������
	private Socket socket;	
	private boolean forceStop;	//�Ƿ�ǿ��ֹͣ
	private boolean working;//�ж��Ƿ�ֹͣ����
	private Timer timer;//��ʱ��
	public SendThread(ClientMainWindow mw){
		this.mw = mw;
		initAllData();
		sndpkt = new ArrayList<Packet>();
		boolean exception = false;
		try {
			socket = new Socket(C.server.ipAddress, C.server.port);
		} catch (UnknownHostException e) {
			mw.appendMessage("�޷������������ӣ����˳�����");
			exception = true;
			e.printStackTrace();
		} catch (IOException e) {
			mw.appendMessage("�޷������������ӣ����˳�����");
			exception = true;
			e.printStackTrace();
		}
		if(!exception){
			mw.appendMessage("�Ѿ������������ӣ�������ʼģ��GBN");
			new SendDataThread().start();
		}
	}
	/*
	 * ��ʼ����Ҫ��������
	 */
	private void initAllData(){
		allData = new String[PACKET_AMOUNT];
		for(int i = 0; i < PACKET_AMOUNT; ++i){
			allData[i] = Md5.md5(Math.random() + "");
			System.out.println("��"+i+"����"+allData[i]);
		}
	}

	public int getNextSendNum() {
		return nextSendNum;
	}

	public void setNextSendNum(int nextSendNum) {
		this.nextSendNum = nextSendNum;
	}
	/*
	 * 
	 */
	private boolean rdtSend(String data){
		if(nextSeqNum < base + N){
			Packet p = makePacket(nextSeqNum, data);
			sndpkt.add(p);
			udtSend(p);
			mw.appendMessage("packet" + p.getSequenceNumber() + "��������");
			if(base == nextSendNum){
				startTimer();
			}
			nextSeqNum++;
			return true;
		}else{
			refuseData(data);
			return false;
		}
	}
	
	/**
	 * ���
	 * @param nextSeqNum
	 * @param data
	 * @return
	 */
	private Packet makePacket(int nextSeqNum, String data){
		Packet p = new Packet();
		p.setData(data);
		p.setSequenceNumber(nextSeqNum);
		p.setSourceIP(C.client.ipAddress);
		p.setSourcePort(C.client.port);
		p.setDestinationIP(C.server.ipAddress);
		p.setDestinationPort(C.server.port);
		p.setCorrectChecksum();
		//��������һ�����ݿ�ʱ����flag��Ϊ0
		if(nextSeqNum == PACKET_AMOUNT - 1){
			p.setFlag(0);
			mw.appendMessage("������һ�����ݿ�");
		}else{
			p.setFlag(1);
		}
		return p;
	}
	/*
	 * ����������,�ܾ���������
	 */
	private void refuseData(String data)
	{
		mw.appendMessage("����������,�ܾ���������:" + data);
	}
	/*
	 * 	
	 */
	private void udtSend(Packet p){
		mw.appendMessage("����packet" + p.getSequenceNumber());
		double prob = Math.random();
		//10%���ʶ���,�ÿͻ�����Ϊ�Ѿ����ͣ�ʵ�ʸ���û�з��ͣ���Ȼ��ʧ
		if(prob < 0.1){
			mw.appendMessage("packet" + p.getSequenceNumber() + "ģ�ⶪ��");
		}
		prob = Math.random();
		//10%�����յ����ŵ���checksum����
		if(prob < 0.1){
			p.setWrongChecksum();
		}else{
			p.setCorrectChecksum();
		}
		try {
			ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
			oos.writeObject(p);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	/*
	 * �жϽ��յ���ACK���Ƿ��ƻ�
	 * ���ƻ��ˣ�����false
	 * û�б��ƻ�������true
	 */
	private boolean rdtRcv(Packet p)
	{
		if(p != null)
			return true;
		else
			return false;
	}
	/*
	 * �޴�ACK����ж�ackNo�Ƿ�δ��ɷ����йأ������checksum�Ƿ���ȷ
	 * ackNo��δ��ɷ����޹أ�������ACK
	 * ackNo��δ��ɷ����йأ�������һ��
	 */
	private boolean notCorrupt(Packet p)
	{
		if(p == null){
			return false;
		}
		if(p.getChecksum() == p.countChecksum()){
			mw.appendMessage("���յ�ack" + p.getSequenceNumber() + "��checksum��ȷ");
		}else{
			mw.appendMessage("���յ�ack" + p.getSequenceNumber() + "��checksum����");
		}
		return true;
	}
	
	private void startTimer(){
		if(timer != null){
			timer.stop();
		}
		timer = new Timer(C.client.timeDelay, new Timeout());
		timer.start();
		mw.appendMessage("��ʼ��ʱ��");
	}
	/*
	 * ֹͣ��ʱ
	 */
	private void stopTimer(){
		if(timer != null){
			timer.stop();
		}
		mw.appendMessage("ֹͣ��ʱ");
	}
	/*
	 * ǿ��ֹͣsocket
	 */
	public void forceStop(){
		forceStop = true;
		if(socket != null){
			try {
				socket.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		timer.stop();
	}
	@Override
	public void run() {
		while(!forceStop){
			Packet p = null;
			try {
				ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
				Object obj = ois.readObject();
				if(obj instanceof Packet){
					p = (Packet) obj;
					System.out.println("��ȡack");
					if(!rdtRcv(p)){
						mw.appendMessage("���յ�һ���𻵵�packet��������");
					}
					if(notCorrupt(p) && p.getSequenceNumber() >= base){
						if(p.getSequenceNumber() == PACKET_AMOUNT - 1){
							working = false;
							mw.appendMessage("���յ����һ������ACK����ɴ��䡣");
							stopTimer();
						}
						base = p.getSequenceNumber() + 1;
						if(base == nextSeqNum){
							stopTimer();
						}else{
							startTimer();
							mw.appendMessage("������ʱ��");
						}
					}
				}
				
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				if(e instanceof SocketException){
					break;
				}
			}
		}
	}
	/*
	 * ��ʱ����
	 */
	class Timeout implements ActionListener {
		
		public void actionPerformed(ActionEvent e){//�ڼ�ʱ����ʱ�������
			startTimer();
			mw.appendMessage("��ʱ����" + base + "�ط�");
			for(int i = base;i < nextSeqNum; ++i)
			{
				udtSend(sndpkt.get(i));
			}
		}
	}
	/*
	 * ��������
	 */
	class SendDataThread extends Thread{

		@Override
		public void run() {
			while(nextSeqNum < PACKET_AMOUNT || working){
				if(forceStop){
					break;
				}
				if(rdtSend(allData[nextSendNum])){
					nextSendNum++;
				}
				try {
					Thread.sleep(C.client.sendInterval);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		
	}
}
