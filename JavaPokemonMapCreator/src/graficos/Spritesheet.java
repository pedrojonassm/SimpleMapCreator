package graficos;

import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

public class Spritesheet {

	private BufferedImage spritesheet;
	private int tamanho, quadradosX, quadradosY, spritesPorSkin;
	String p;
	public Spritesheet(String path, int t) {
		tamanho = t;
		p = path;
		try {
			spritesheet = ImageIO.read(getClass().getResource(path));
			quadradosX = spritesheet.getWidth()/tamanho;
			quadradosY = spritesheet.getHeight()/tamanho;
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public BufferedImage getAsset(int x, int y){
        return getAsset(x + y*tamanho);
    }
	public BufferedImage getAsset(int position){
        return spritesheet.getSubimage((position%quadradosX)*tamanho, (position/quadradosX)*tamanho, tamanho, tamanho);
    }
	
	public BufferedImage[] get_x_sprites(int total) {
		BufferedImage[] retorno = new BufferedImage[total];
		for (int i = 0; i < total; i++) {
			retorno[i] = getAsset(i);
		}
		return retorno;
	}
	
	public int getQuadradosX() {
        return quadradosX;
    }
	public int getQuadradosY() {
        return quadradosY;
    }

    public int getTamanho() {
        return tamanho;
    }
    
    public int getSpritesPorSkin() {
		return spritesPorSkin;
	}
}