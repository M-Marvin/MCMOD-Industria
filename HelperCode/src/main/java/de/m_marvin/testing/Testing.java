package de.m_marvin.testing;

import de.m_marvin.unimat.impl.Quaterniond;
import de.m_marvin.univec.impl.Vec3d;
import de.m_marvin.univec.impl.Vec4d;

public class Testing {
	
	public static void main(String... args) {
		//new Vec3d(1.9999999931468058,8.045529741611852E-5,-1.4470563819479724E-4)
		Vec3d torque = calcTorque(new Vec3d(1, 0, 0), new Vec3d(0, 0, 1));
		
		//System.out.println(torque);
		
	}
	
	public static Vec3d calcTorque(Vec3d current, Vec3d target) {
		
		double angle = Math.acos(target.dot(current));
		Vec3d axis = target.cross(current).normalize();
		Quaterniond rotation = new Quaterniond(axis, angle);
		
		Quaterniond a = new Quaterniond(current.x, current.y, current.z, 0.0);
		Quaterniond b = rotation;
		
		Vec4d r = new Vec4d(
				a.i*b.i - a.j*b.j - a.k*b.k - a.r*b.r,
				a.i*b.j - a.j*b.i - a.k*b.r - a.r*b.k,
				a.i*b.k - a.j*b.r - a.k*b.i - a.r*b.j,
				a.i*b.r - a.j*b.k - a.k*b.j - a.r*b.i
				);
		Vec3d result = new Vec3d(r.x, r.y, r.z).normalize();
		
		
		System.out.println("DD " + result);
		
		double pitch = Math.atan2(2*rotation.i*rotation.r - 2*rotation.j*rotation.k, 1 - 2*rotation.i*rotation.i - 2*rotation.k*rotation.k); 
		double roll = Math.atan2(2*rotation.j*rotation.r - 2*rotation.i*rotation.k, 1 - 2*rotation.j*rotation.j - 2*rotation.k*rotation.k); 
		double yaw = Math.asin(2*rotation.k*rotation.j + 2*rotation.k*rotation.r);
		
		return new Vec3d(pitch, roll, yaw);
		
	}
	
}
