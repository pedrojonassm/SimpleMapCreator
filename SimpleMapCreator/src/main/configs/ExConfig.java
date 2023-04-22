package main.configs;

public class ExConfig {

	private int worldWidth, worldHeight, worldHigh, TileSize, playerX, playerY, playerZ, totalLayers;

	public int getWorldWidth() {
		return worldWidth;
	}

	public void setWorldWidth(int worldWidth) {
		this.worldWidth = worldWidth;
	}

	public int getWorldHeight() {
		return worldHeight;
	}

	public void setWorldHeight(int worldHeight) {
		this.worldHeight = worldHeight;
	}

	public int getWorldHigh() {
		return worldHigh;
	}

	public void setWorldHigh(int worldHigh) {
		this.worldHigh = worldHigh;
	}

	public int getTileSize() {
		return TileSize;
	}

	public void setTileSize(int tileSize) {
		TileSize = tileSize;
	}

	public int getPlayerX() {
		return playerX;
	}

	public void setPlayerX(int playerX) {
		this.playerX = playerX;
	}

	public int getPlayerY() {
		return playerY;
	}

	public void setPlayerY(int playerY) {
		this.playerY = playerY;
	}

	public int getTotalLayers() {
		return totalLayers;
	}

	public void setTotalLayers(int totalLayers) {
		this.totalLayers = totalLayers;
	}

	public int getPlayerZ() {
		return playerZ;
	}

	public void setPlayerZ(int playerZ) {
		this.playerZ = playerZ;
	}

	public void fromConfig(Configs prConfigs) {
		setWorldWidth(prConfigs.getWorldWidth());
		setWorldHeight(prConfigs.getWorldHeight());
		setWorldHigh(prConfigs.getWorldHigh());
		setTileSize(prConfigs.getTileSize());
		setPlayerX(prConfigs.getPlayerX());
		setPlayerY(prConfigs.getPlayerY());
		setPlayerZ(prConfigs.getPlayerZ());
		setTotalLayers(prConfigs.getTotalLayers());
	}

}
