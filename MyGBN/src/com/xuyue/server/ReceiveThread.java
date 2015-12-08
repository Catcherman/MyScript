/**
 * 
 */
package com.xuyue.server;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketException;

import com.xuyue.common.C;
import com.xuyue.common.Packet;
import com.xuyue.server.ServerMainWindow;

/**
 * @author xuyue
 *
 */
public class ReceiveThread extends Thread {

	private ServerMainWindow mw;
	private Socket socket;
	private Packet packet;
	public ReceiveThread(Socket socket, ServerMainWindow mw) {
		this.mw = mw;
		this.socket = socket;
	}
	@Override
	public void run() {
		while(true){
				Object obj;
				ObjectInputStream ois ;
				try {
					ois = new ObjectInputStream(socket.getInputStream());
					obj = ois.readObject();
					if(!(obj instanceof Packet)){
						mw.appendMessage("����packetʧ�ܣ�packet�Ѿ��𻵣��޷�������");
					}
					packet = (Packet) obj;
					mw.appendMessage("���յ�packet������" + packet.getSourceIP() + ":"
							+ packet.getSourcePort() + ",���к�Ϊ" + packet.getSequenceNumber());
					handlePacket(packet);
				} catch (ClassNotFoundException e) {
					// TODO Auto-generated catch block
					mw.appendMessage("����packetʧ�ܣ�");
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					mw.appendMessage("����packetʧ�ܣ�");
					e.printStackTrace();
					if(e instanceof SocketException || e instanceof EOFException){
						break;
					}
				}
		}
		
	}

	private void handlePacket(Packet p){
		if(p.getSequenceNumber() == mw.getNextExceptSequence()){
			mw.appendMessage("���յ����ڵȴ���packet" + p.getSequenceNumber() + ",׼������checksum");
			if(p.getChecksum() == p.countChecksum()){
				responseACK(p.getSequenceNumber());
				if(p.getFlag() == 1){
					mw.appendMessage("packet" + p.getSequenceNumber() + "checksum��ȷ�����ϲ��ύ����" + p.getData() + ",�ظ�ACK,�ȴ���һ����");
					mw.setNextExceptSequence(mw.getNextExceptSequence() + 1);
				}else{
					mw.appendMessage("packet" + p.getSequenceNumber() + "checksum��ȷ" + p.getData() + ",�ظ�ACK,���δ�����ɡ�");
				}
				
			}else {
				mw.appendMessage("packet" + p.getSequenceNumber() + "checksum�����ظ���һ��ACK,�����ð���");
				responseACK(mw.getNextExceptSequence() - 1);
			}
		}else{
			mw.appendMessage("���ڵȴ����к�Ϊ" + mw.getNextExceptSequence() + "��packet�������յ�" + p.getSequenceNumber() + ",�����ð����ظ���һ��ACK");
			responseACK(mw.getNextExceptSequence() - 1);
		}
		
	}
	/*
	 * ����Ӧ���ACK
	 */
	private void responseACK(int num){
		try {
			double prob = Math.random();
			//ģ�ⶪ��
			if(prob < C.server.probilityLose){
				mw.appendMessage("ACK" + num + "ģ�ⶪ����");
				return;
			}
			Packet p = new Packet();
			p.setSourceIP(C.server.ipAddress);
			p.setSourcePort(C.server.port);
			p.setDestinationIP(C.client.ipAddress);
			p.setDestinationPort(C.client.port);
			p.setSequenceNumber(num);
			p.setData("ack");
			prob = Math.random();
			//�ܸ���
			if(prob < C.server.probilityDisturb){
				p.setWrongChecksum();
			}else{
				p.setCorrectChecksum();
			}
			ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
			oos.writeObject(p);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
