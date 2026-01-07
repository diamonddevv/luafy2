# luafy2
modern rewrite of luafy

## writing scripts
i reccommend the use of [Lua Language Server](https://luals.github.io/) when writing scripts for this mod.
The mod generates the correct library file that allows for autocompletion and assistance when writing scripts
using an IDE such as Vscode at `<game directory>/luafy_scripts/autodoc.lua`.

### luaj
this mod uses the FiguraMC implementation of Lua.

---

# rewrite goals
- lua scripts system
  - [x] loader
  - [x] executor
    - [ ] with parameters
    - [ ] threaded
  - [x] events
- APIs / game interfaces
  - [ ] entity
  - [ ] block
  - [ ] item
  - [ ] world/server
  - [x] math (vectors, mainly)
  - [ ] file (abstract read/write to file)
- other
  - [x] autogen documentation (actively working on)

## what im not worried about right now
- scripting language independence; im just focusing on lua
- client sided stuff. there is a client source, but im not doing anything with it yet
- sandboxing. run anything at your own risk


# licensing
luafy is placed under the **CC0-1.0** license, meaning you can basically do whatever you
like with it. _(this is not legal advice)_

if you use it in anything, credit is not required but very much appreciated!
(also, feel free to let me know! id love to see what people do with this!)