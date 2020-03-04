package staticUtils;

public class MathUtils {
	public static double pi = Math.PI;
	public static double halfPi = pi/2;
	public static double tau = 2*Math.PI;
	
	public static double sin(double theta) {
		theta = mod(theta, tau);
		if (theta < halfPi) return Math.sin(theta);
		if (theta == halfPi) return 1;
		if (theta > halfPi && theta < pi) return Math.sin(pi-theta);
		if (theta == pi) return 0;
		if (theta > pi && theta < 3*halfPi) return -Math.sin(theta-pi);
		if (theta == 3*halfPi) return -1;
		return -Math.sin(tau-theta);
	}
	
	public static double cos(double theta) {
		theta = mod(theta,tau);
		if (theta < halfPi) return Math.cos(theta);
		if (theta == halfPi) return 0;
		if (theta > halfPi && theta < pi) return -Math.cos(pi-theta);
		if (theta == pi) return -1;
		if (theta > pi && theta < 3*halfPi) return -Math.cos(theta-pi);
		if (theta == 3*halfPi) return 0;
		return Math.cos(tau-theta);
	}
	
	public static double mod(double divisor, double dividend) {
		if (divisor > 0) return divisor % dividend;
		return dividend + (divisor % dividend);
	}
	
	private static double incrSqrt(double x) {
		return Math.sqrt(1 + x*x);
	}
	
	public static double hyperbolicSquish(double x) {
		return 0.5*(x + incrSqrt(x));
	}
	
	public static double sqrtHyperSquish(double x) {
		return Math.sqrt(hyperbolicSquish(x));
	}
	
	public static double sigmoid(double x) {
		x = 0.5*x;
		return 0.5*(1 + x/incrSqrt(x));
	}

}
