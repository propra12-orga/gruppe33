package propra2012.gruppe33.graphics.assets;

import java.io.Serializable;

public interface AssetLoader<T> extends Serializable {

	T loadAsset(AssetManager assetManager, String assetPath) throws Exception;
}
