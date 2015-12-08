package com.xuyue.dijkstra;

import java.util.Random;

import com.xuyue.grap.Grap;

public class Dijkstra {
	public static int min(int[] a) {

		// ���������е���Сֵ���±�
		int min = a[0];
		int minsign = 0;
		for (int i = 0; i < a.length; i++) {
			if (a[i] != 0) {
				if (a[i] < min) {
					min = a[i];
					minsign = i;
				}
			}
		}

		return minsign;

		// min��ʾ��Сֵ��minsign��ʾ��Ӧ��Ԫ���±�

	}

	public static String[] closestpath(String[] route, String[] p, int star) { // ����������·�����ұ������ַ���������
		int num = route.length; // ��ȡ�ڵ���
		int sorce = star;

		// ��ȡԴ�ڵ�
		String[] vname = route;

		// ��ȡ���㼯
		String[] path = new String[num]; // ·��
		for (int i = 0; i < num; i++)
			path[i] = vname[i] + " ";
		String p1 = "AB";
		for (int i = 0; i < num; i++) {
			if (i != sorce) {
				p1 = p[i];

				// ��������˼ά���Ӻ���ǰ���α�ʾ

				path[i] += p1 + " ";
				while (p1 != null && (!p1.equals(vname[sorce]))) {
					// ������Դ�ڵ��ʾ���·����ȫȷ����
					for (int t = 0; t < num; t++) {
						if (vname[t].equals(p1))
							p1 = p[t];
					}

					path[i] += p1 + " ";
				}

			}

		}
		String[][] path01 = new String[num][];

		// ���²��־���·����ʾ��ʽ�ĵ�����ת��
		String[] truepath = new String[num];
		for (int i = 0; i < num; i++)
			truepath[i] = "";
		for (int i = 0; i < num; i++)
			path01[i] = path[i].split(" ");
		for (int i = 0; i < num; i++)
			path01[i][path01[i].length - 1] = route[sorce];
		for (int i = 0; i < num; i++) {
			for (int j = path01[i].length - 1; j >= 0; j--) {
				if (path01[i][j] != null)
					truepath[i] += path01[i][j] + "  ";
			}
		}
		for (int i = 0; i < num; i++) {
			truepath[i] = truepath[i].replaceAll("  ", "��");
			truepath[i] = truepath[i].substring(0, truepath[i].length() - 1);
		}
		return truepath;
	}

	public static void dijkstra(Grap g, String star) {
		// ��ȡԴ�ڵ㵽���ڵ���С������㷨
		int vnum = g.getRoute().length; // ��ȡ�ڵ���
		String source = star;
		// ��ȡԴ�ڵ�
		String[] v = g.getRoute();

		// ��ȡ���㼯
		int[][] dis = g.getDistance();
		// ��ȡ������������ֵ��Ȩֵ��
		int sorce;
		for (sorce = 0; sorce < vnum; sorce++)
			if (v[sorce].equals(source))

				break;

		// ����Դ�ڵ�
		int[] D = new int[vnum];

		// ������ʾԴ�ڵ㵽Ŀ��ڵ㵱ǰ����̾���
		String[] p = new String[vnum];

		// ��ʾԴ�ڵ㵽Ŀ��ڵ㵱ǰ���·���ϵ�ǰһ�ڵ�
		int[] closest = new int[vnum];

		// �����̾���
		for (int i = 0; i < vnum; i++)
			closest[i] = 10000;
		closest[sorce] = 0;
		for (int i = 0; i < vnum; i++) {
			if (i != sorce) {
				D[i] = dis[sorce][i];
				// ��ʼ��dijkstra�㷨���һ��
				p[i] = source;

			}

		}
		D[sorce] = 10000;
		int sv = 1;

		// ��־��ǰ�Ѿ����뼯�ϵĽڵ����
		while (sv < vnum) {
			int sign = min(D); // ��ȡ���о���ֵ����С��һ����·�����±�
			sv++;

			// �Ѹýڵ���뵽�����У�
			for (int i = 0; i < vnum; i++) {
				if (i != sorce && D[i] != 9999) {

					// �Ѿ����ǹ��Ľڵ㲻�ٿ��Ƿ�Χ��
					if (D[i] > dis[sign][i] + D[sign]) {
						D[i] = dis[sign][i] + D[sign];

						// ������С����ı�
						p[i] = v[sign];

						// �������·����ǰһ���ڵ�
					}
				}
			}
			closest[sign] = D[sign];
			D[sign] = 9999;
		}
		String[] closestpath = closestpath(v, p, sorce);

		// �ڻ������С������������Ҷ�Ӧ����̾���

		for (int i = 0; i < vnum; i++) {
			if (i != sorce && closest[i] < 9999) {

				// ��ʾԴ�ڵ㵽��Ŀ��ڵ����С��������·��
				System.out.print("�����㡿·��" + source + "��·��" + v[i] + "����̾�����"
						+ closest[i]);
				System.out.print("   ");
				System.out.print("���·��Ϊ�� " + closestpath[i]);
				System.out.println();
			} else {
				if (!source.equals(v[i])) {

					// �����ɴ������ʾ
					System.out.print("�����㡿·��" + source + "���ܵ���·��" + v[i]);
					System.out.println();
				}
			}
		}

	}

	public static void main(String[] args) {
		int num = 0;

		while (num == 0)
			num = new Random().nextInt(22);

		// �趨·�������������ֵ
		String[] route = new String[num];

		// ��ʼ�����㼯
		for (int i = 0; i < num; i++)
			route[i] = i + ""; // ��ʼ����������
		Grap g = new Grap(route);

		// ��ʼ��ͼ
		int sorce = new Random().nextInt(num);

		// ���ѡȡԴ�ڵ�
		System.out.println("�������ѡȡ��Դ�ڵ���·��" + route[sorce]);
		dijkstra(g, route[sorce]);
		// ����Dijkstra�㷨
	}

}