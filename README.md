# luafy2
modern rewrite of luafy


## luaj
this mod uses the FiguraMC implementation of Lua.

---

# rewrite goals
- lua scripts system
  - [ ] loader
  - [ ] executor
    - [ ] with parameters
    - [ ] threaded
  - [ ] events
- APIs / game interfaces
  - [ ] entity
  - [ ] block
  - [ ] item
  - [ ] world/server
  - [ ] math (vectors, mainly)
  - [ ] file (abstract read/write to file)
- other
  - [ ] autogen documentation

## what im not worried about right now
- scripting language independence; im just focusing on lua
- client sided stuff. there is a client source, but im not doing anything with it yet
- sandboxing. run anything at your own risk