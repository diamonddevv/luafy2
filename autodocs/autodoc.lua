---@meta luafyautodoc

--#region Information
-- GENERATED AUTODOC
-- Generated: 2026-01-11T14:02:13.116597400
-- Luafy Version: 2.0.0
-- Format: Lua LS library file
--#endregion

---@type table
ctx = {}

--#region Enums

---@enum (key) Note
Note = {
	G_FLAT_LOW = "G_FLAT_LOW",
	A_FLAT_LOW = "A_FLAT_LOW",
	E_LOW = "E_LOW",
	F_SHARP_LOW = "F_SHARP_LOW",
	B_HIGH = "B_HIGH",
	B_FLAT_HIGH = "B_FLAT_HIGH",
	C_SHARP_HIGH = "C_SHARP_HIGH",
	E_HIGH = "E_HIGH",
	D_LOW = "D_LOW",
	C_SHARP_LOW = "C_SHARP_LOW",
	G_FLAT_HIGH = "G_FLAT_HIGH",
	D_HIGH = "D_HIGH",
	F_SHARP_HIGH = "F_SHARP_HIGH",
	G_HIGH = "G_HIGH",
	A_FLAT_HIGH = "A_FLAT_HIGH",
	C_LOW = "C_LOW",
	A_HIGH = "A_HIGH",
	G_LOW = "G_LOW",
	C_HIGH = "C_HIGH",
	F_HIGH = "F_HIGH",
	A_LOW = "A_LOW",
	B_LOW = "B_LOW",
	D_SHARP_HIGH = "D_SHARP_HIGH",
	B_FLAT_LOW = "B_FLAT_LOW",
	F_LOW = "F_LOW",
	D_SHARP_LOW = "D_SHARP_LOW",
}

---@enum (key) Instrument
Instrument = {
	VIBRAPHONE = "VIBRAPHONE",
	BASS = "BASS",
	HIHAT = "HIHAT",
	DIDGERIDOO = "DIDGERIDOO",
	XYLOPHONE = "XYLOPHONE",
	GLOCKENSPIEL = "GLOCKENSPIEL",
	BIT = "BIT",
	CHIME = "CHIME",
	PLING = "PLING",
	SNARE = "SNARE",
	GUITAR = "GUITAR",
	BANJO = "BANJO",
	BASS_DRUM = "BASS_DRUM",
	HARP = "HARP",
	COWBELL = "COWBELL",
	FLUTE = "FLUTE",
}

--#endregion

--#region Script Object

--- Mathematical 3D Vector
---@class Vec3d
---@field x number x component
---@field y number y component
---@field z number z component
local Vec3d = {}


--- An object representing a mod installed on the server.
---@class Mod
---@field modid string The id of this mod.
---@field version string The version of the mod currently installed.
local Mod = {}


--- Object representing the potential result of a script execution. Since scripts run asynchronously, this object allows for a result to be awaited if needed.
---@class ScriptResult
local ScriptResult = {}

--- Awaits this script to complete execution if it has not already, and returns the result.
---@return any | nil
function ScriptResult.await_result() end

--- Awaits this script to complete execution if it has not already, and returns if it succeeded.
---@return boolean
function ScriptResult.await_success() end

--- Awaits this script to complete execution if it has not already, and returns the error string if it failed, or nil if it succeeded.
---@return string | nil
function ScriptResult.await_error() end

--- Releases the internal Result Java object from the cache. Using this object after this has been called may result in an error.
---@return nil
function ScriptResult.release() end


--- An entity.
---@class Entity
---@field name string Entity's name.
---@field uuid string Entity's uuid.
local Entity = {}

--- Gets the entity's current position.
---@return Vec3d
function Entity.get_pos() end

--- Returns true if this entity is a LivingEntity.
---@return boolean
function Entity.is_living() end

--- Return this entity as a LivingEntity.
---@return LivingEntity
function Entity.as_living() end


--- A living entity.
---@class LivingEntity: Entity
local LivingEntity = {}

--- Returns this entities health.
---@return number
function LivingEntity.get_health() end

--- Damages this entity.
---@param damage_type string Identifier of a damage type.
---@param amount number Amount of damage to deal.
---@param source Entity | nil Optional entity that dealt this damage.
---@return nil
function LivingEntity.hurt(damage_type, amount, source) end

--- Kills this entity.
---@param damage_type string Identifier of a damage type.
---@param source Entity | nil Optional entity that killed this one.
---@return nil
function LivingEntity.kill(damage_type, source) end

--- Teleports this entity to the specified position
---@param pos Vec3d Position to teleport to.
---@param yaw number | nil Yaw angle of entity after teleporting. Defaults to current yaw.
---@param pitch number | nil Pitch angle of entity after teleporting. Defaults to current pitch.
---@param retain_velocity boolean | nil If true, the entity will retain their velocity after teleporting. Defaults to true.
---@param dimension_id string | nil Identifier of dimension to teleport to. Defaults to the entities current dimension.
---@return nil
function LivingEntity.teleport(pos, yaw, pitch, retain_velocity, dimension_id) end


--- A player.
---@class Player: LivingEntity
local Player = {}

--- Prints a line to this player's chat.
---@param msg string String to display.
---@return nil
function Player.tell(msg) end


--- A block type.
---@class Block
local Block = {}


--- An item type.
---@class Item
local Item = {}


--- An item stack.
---@class ItemStack
local ItemStack = {}


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

--- Plays the specified noteblock note at the given location.
---@param note Note Note to play
---@param instrument Instrument Instrument
---@param pos Vec3d Location to play sound at
---@param particle boolean If true, a particle will also render. Defaults to true.
---@return nil
function minecraft.note(note, instrument, pos, particle) end

--- Waits for a given number of seconds before continuing.
---@param seconds number Number of seconds to wait.
---@return nil
function minecraft.sleep(seconds) end

local entities = {}

--- Uses an entity selector to find a player.
---@param selector string Entity selector
---@return Player
function entities.get_player_from_selector(selector) end

--- Uses an entity selector to find an entity.
---@param selector string Entity selector
---@return Entity
function entities.get_entity_from_selector(selector) end

--- Uses an entity selector to find several entities.
---@param selector string Entity selector
---@return Entity[]
function entities.get_entities_from_selector(selector) end

minecraft.entities = entities

luafy = {}

--- Executes the script with the given identifier, and awaits its completion. Returns future result, that can be awaited if needed.
---@param script string Identifier of script to be executed.
---@param context table | nil Context to pass to script. Defaults to an empty table.
---@return ScriptResult
function luafy.script(script, context) end

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
---@return Vec3d
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
---@return Mod
function fabric.get_mod(modid) end

--- Returns a list of all the mods that are installed.
---@return string[]
function fabric.get_mods() end

--#endregion

--#region Script Event

-- luafy:load: Executes after a reload. ; this generator does not currently provide additional information.
-- luafy:tick: Executes every server tick. ; this generator does not currently provide additional information.
--#endregion

