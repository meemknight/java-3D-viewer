# Java project, build on top of this [repo](https://github.com/meemknight/javaGameSetup), using [LWJGL](https://www.lwjgl.org).


![](https://github.com/meemknight/photos/blob/master/java.jpg)

---

3D PBR renderer built from scratch.
Features:

- All common types of lights (limited only by vram and performance).
- [Normal Mapping](https://en.wikipedia.org/wiki/Normal_mapping).
- [PBR](https://learnopengl.com/PBR/Theory) lighting model with [IBL](https://learnopengl.com/PBR/IBL/Diffuse-irradiance) and multiple scattering as described in [this](https://jcgt.org/published/0008/01/03/) article.
- Gamma corrected and HDR [Filmic Tonemapping](//https://github.com/TheRealMJP/BakingLab/blob/master/BakingLab/ACES.hlsl).
- Materials.

---

Actions: 

- Render a 3D model. (Renderer class)
- Render the sky box. (SkyBoxRenderer class)
- Load Textures and [sky boxes](https://en.wikipedia.org/wiki/Skybox_(video_games)). (TextureLoader class) 
- Load shader programs. (Shader class)
- Load a 3D model. (ModelLoader class)
- Create the [irradiance map](https://learnopengl.com/PBR/IBL/Diffuse-irradiance) and the [pre-filtered environment map](https://learnopengl.com/PBR/IBL/Specular-IBL) for sky boxes. (TextureLoader class generateSkyBoxConvoluteTextures method)
- Render a preview of the position of the lights. (GyzmosRenderer class)
- Add lights to the scene. (LightManager class)
- Remove lights from the scene. (LightManager class)
- Update existing lights. (LightManager class)
