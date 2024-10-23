package de.m_marvin.testing;

import de.m_marvin.univec.impl.Vec2f;

public class Testing {
	
	public static void main(String... args) {
		
		Vec2f startPos = new Vec2f(0, 10);
		Vec2f endPos = new Vec2f(10, 0);
		Vec2f startVec = new Vec2f(1, 0);
		Vec2f endVec = new Vec2f(0, 1);
		
		Vec2f[] vecs = makeLine(startPos, startVec, endPos, endVec, 1.0F);
		
		Vec2f p = startPos;
		for (Vec2f vec : vecs) {
			System.out.println(vec);
			p.addI(vec);
		}
		System.out.println(p);
		
	}
	
	public static Vec2f calculateBezier(Vec2f[] points, float s) {
		while (points.length > 1) {
			Vec2f[] p = new Vec2f[points.length - 1];
			for (int i = 0; i < p.length; i++) {
				p[i] = points[i + 1].sub(points[i]).mul(s).add(points[i]);
			}
			points = p;
		}
		return points[0];
	}
	
	public static Vec2f[] makeLine(Vec2f p1, Vec2f v1, Vec2f p2, Vec2f v2, float vecmaxlen) {
		Vec2f p1b = p2.sub(p1).mul(v1).add(p1);
		Vec2f p2b = p1.sub(p2).mul(v2).add(p2);
		Vec2f[] points = new Vec2f[] {p1, p1b, p2b, p2};
		float distance = p1.dist(p2);
		Vec2f[] vecs = new Vec2f[Math.round(distance / vecmaxlen)];
		
		Vec2f lp = p1;
		for (int i = 0; i < vecs.length; i++) {
			float s = (i + 1) / (float) vecs.length;
			Vec2f p = calculateBezier(points, s);
			
			System.out.println(s + " \t\t" + p);
			
			vecs[i] = p.sub(lp);
			lp = p;
		}
		
		return vecs;
	}
	
}
