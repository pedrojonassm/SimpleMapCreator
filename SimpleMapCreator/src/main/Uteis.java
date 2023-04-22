package main;

import world.Camera;
import world.World;

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

	public static int log(int prValor, int prLogaritmo) {
		int k = 0;
		while (prValor != 0 && prValor % prLogaritmo == 0) {
			k++;
			prValor = prValor / prLogaritmo;
		}
		return k;
	}

	public static int[] calcularPosicaoSemAlturaIgnorandoCamera(int prPos) {
		int[] retorno = { 0, 0, 0 };
		retorno[0] = (int) ((prPos % (World.WIDTH * World.HIGH)) / World.HIGH) * Gerador.TS - Camera.x;
		retorno[1] = (int) (prPos / World.HEIGHT / World.HIGH) * Gerador.TS - Camera.y;
		retorno[2] = (prPos % World.HIGH);
		return retorno;
	}

	public static int[] calcularPosicaoSemAltura(int prPos) {
		int[] retorno = { 0, 0, 0 };
		retorno[0] = (int) ((prPos % (World.WIDTH * World.HIGH)) / World.HIGH) * Gerador.TS;
		retorno[1] = (int) (prPos / World.HEIGHT / World.HIGH) * Gerador.TS;
		retorno[2] = (prPos % World.HIGH);
		return retorno;
	}

	public static int[] calcularPosicaoComAlturaIgnorandoCamera(int prPos) {
		int[] retorno = calcularPosicaoSemAlturaIgnorandoCamera(prPos);
		int lSubtract = (prPos % World.HIGH) * Gerador.TS;
		retorno[0] -= lSubtract;
		retorno[1] -= lSubtract;
		return retorno;
	}

}
