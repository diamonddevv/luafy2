---@meta luafyautodoc

--#region Information
-- GENERATED AUTODOC
-- Generated: 2026-01-07T19:57:43.422114700
-- Luafy Version: 2.0.0
-- Format: Lua LS library file
--#endregion

---@type table
ctx = {}

--#region Script Object

--- Mathematical 3D Vector
---@class vec3d
---@field x number x component
---@field y number y component
---@field z number z component
local vec3d = {}


--- A player.
---@class player
---@field name string Player's username
---@field uuid string Player's uuid. Used internally to reference the player back from this LuaValue.
local player = {}

--- Gets the player's current position.
---@return vec3d
function player.get_pos() end

--- Prints a line to this player's chat.
---@param msg string String to display.
---@return nil
function player.tell(msg) end


--- An object representing a mod installed on the server.
---@class mod
---@field modid string The id of this mod.
---@field version string The version of the mod currently installed.
local mod = {}


--#endregion

--#region Script Api

minecraft = {}

--- Returns the current Minecraft version string.
---@return string
function minecraft.get_version() end

--- Prints an unformatted line to the server chat, visible to all players. (similar to /tellraw). Also prints to the console.
---@param message string Message to be printed.
---@return nil
function minecraft.say(message) end

--- Executes the given command from the server command source. Returns the result of the command.
---@param command string Command to be executed.
---@return integer
function minecraft.command(command) end

--- Uses an entity selector to find a player.
---@param selector string Entity selector
---@return player
function minecraft.get_player_from_selector(selector) end

luafy = {}

--- Executes the script with the given identifier. Returns the value returned from this script.
---@param script string Identifier of script to be executed.
---@return any
function luafy.script(script) end

--- Returns the context table for this script. The contents of this table depend on the event that called it, or the values passed by /luafy. This is the same as the global table `ctx`.
---@return table
function luafy.context() end

--- Returns a random hello world, as the mod does when Minecraft boots.
---@return string
function luafy.provide_hello_world() end

--- Returns the version of LuaJ used by the mod.
---@return string
function luafy.get_luaj_version() end

math = {}

--- Creates a 3-component vector object.
---@param x number x component
---@param y number y component
---@param z number z component
---@return vec3d
function math.vec3d(x, y, z) end

fabric = {}

--- Returns the current Fabric version string.
---@return string
function fabric.get_version() end

--- Returns true if the specified mod exists.
---@param modid string A mod id.
---@return boolean
function fabric.has_mod(modid) end

--- Returns an object representing an installed mod. Returns nil if the specified mod does not exist.
---@param modid string A mod id.
---@return mod
function fabric.get_mod(modid) end

--- Returns a list of all the mods that are installed.
---@return string[]
function fabric.get_mods() end

--#endregion

--#region Script Event

-- luafy:tick ; this generator does not currently provide additional information.

--#endregion

