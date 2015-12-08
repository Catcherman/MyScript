package com.xuyue.grap;

import java.util.Random;

public class Grap {
	private String[] route;
	private int[][] distance;
	private int count = 0;

	public Grap(String[] v) {
		route = v;
		// ��ȡ���㼯
		distance = new int[route.length][route.length];
		// �ö�ά���鱣�����������ߵľ���
		System.out.print("�������������" + v.length + "��·����  "); // ͳ��·��������
		System.out.print("·�����ֱ�Ϊ��  ");
		for (int i = 0; i < v.length; i++)
		{
			System.out.print(v[i]);
			System.out.print("  ");// ������ʾ·������
		}
		System.out.println();
		// �����Լ����Լ��ľ���Ϊ0
		for (int i = 0; i < route.length; i++)
		{
			distance[i][i] = 0;
		}	
		for (int i = 0; i < route.length - 1; i++) 
		{
			System.out.print("�������·��" + route[i] + "������·�ɵľ�������Ϊ�� ");
			for (int j = i + 1; j < route.length; j++) {
				int ran = new Random().nextInt(150);
				// �趨���ֵ
				if (ran > 100) 
				{
					// ����һ���ĸ��ʳ��֡������
					distance[i][j] = 10000;
					System.out.print("   ��");
					distance[j][i] = distance[i][j];
				} 
				else
				{
					distance[i][j] = ran;
					System.out.print("   ");
					System.out.print(ran);
					// ������ʾÿ�����㵽��������ľ���
					distance[j][i] = ran;
					count++;
				}
			}
			System.out.println();
		}
		System.out.print("��ͳ�ơ����ܹ����ܹ��У�  ");
		// ͳ������Ȩֵ�ߵ�����
		System.out.print(count);
		System.out.print("  ���ߣ�");
	}

	public String[] getRoute() {
		return route;
	}

	public void setRoute(String[] route) {
		this.route = route;
	}

	public int[][] getDistance() {
		return distance;
	}

	public void setDistance(int[][] distance) {
		this.distance = distance;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}
	
}
