package main;

public class Uteis {

	public static double distancia(int x1, int x2, int y1, int y2) {
		return Math.sqrt((x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2));
	}

	public static double modulo(double prValor) {
		if (prValor >= 0)
			return prValor;
		else
			return prValor *= -1;
	}

}
