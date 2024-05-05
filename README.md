> Note: Currently, only the 1.20.3+ version of the mod is in active development.
> While the implementation for 1.20.2 exists in the main branch, it was NOT tested.
> 
> To see the code for older versions, check other branches of this repo.

# Instructions on porting
1. Create a subproject with an empty `build.gradle` and appropriate `gradle.properties`.
2. Implement `TweaksMod`, `AbstractPackStateObserver` and `TweaksModMenu`.
3. Implement `ClientLike` on `MinecraftClient` and `ServerInfoLike` on `ServerInfo` accordingly using mixins.
4. Don't forget to add `DisconnectHook` to mixins.