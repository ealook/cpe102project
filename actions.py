import entities
import worldmodel
import pygame
import math
import random
import point
import image_store

BLOB_RATE_SCALE = 4
BLOB_ANIMATION_RATE_SCALE = 50
BLOB_ANIMATION_MIN = 1
BLOB_ANIMATION_MAX = 3

ORE_CORRUPT_MIN = 20000
ORE_CORRUPT_MAX = 30000

QUAKE_STEPS = 10
QUAKE_DURATION = 1100
QUAKE_ANIMATION_RATE = 100

VEIN_SPAWN_DELAY = 500
VEIN_RATE_MIN = 8000
VEIN_RATE_MAX = 17000


def sign(x):
   if x < 0:
      return -1
   elif x > 0:
      return 1
   else:
      return 0


def next_position(world, entity_pt, dest_pt):
   horiz = sign(dest_pt.x - entity_pt.x)
   new_pt = point.Point(entity_pt.x + horiz, entity_pt.y)

   if horiz == 0 or world.is_occupied(new_pt):
      vert = sign(dest_pt.y - entity_pt.y)
      new_pt = point.Point(entity_pt.x, entity_pt.y + vert)

      if vert == 0 or world.is_occupied(new_pt):
         new_pt = point.Point(entity_pt.x, entity_pt.y)

   return new_pt


def blob_next_position(world, entity_pt, dest_pt):
   horiz = sign(dest_pt.x - entity_pt.x)
   new_pt = point.Point(entity_pt.x + horiz, entity_pt.y)

   if horiz == 0 or (world.is_occupied(new_pt) and
      not isinstance(world.get_tile_occupant(new_pt),
      entities.Ore)):
      vert = sign(dest_pt.y - entity_pt.y)
      new_pt = point.Point(entity_pt.x, entity_pt.y + vert)

      if vert == 0 or (world.is_occupied(new_pt) and
         not isinstance(world.get_tile_occupant(new_pt),
         entities.Ore)):
         new_pt = point.Point(entity_pt.x, entity_pt.y)

   return new_pt


def miner_to_ore(world, entity, ore):
   entity_pt = entity.get_position()
   if not ore:
      return ([entity_pt], False)
   ore_pt = ore.get_position()
   if entity_pt.adjacent_to(ore_pt):
      entity.set_resource_count(1 + entity.get_resource_count())
      remove_entity(world, ore)
      return ([ore_pt], True)
   else:
      new_pt = next_position(world, entity_pt, ore_pt)
      return (world.move_entity(entity, new_pt), False)


def miner_to_smith(world, entity, smith):
   entity_pt = entity.get_position()
   if not smith:
      return ([entity_pt], False)
   smith_pt = smith.get_position()
   if entity_pt.adjacent_to(smith_pt):
      smith.set_resource_count(smith.get_resource_count() +
         entity.get_resource_count())
      entity.set_resource_count(0)
      return ([], True)
   else:
      new_pt = next_position(world, entity_pt, smith_pt)
      return (world.move_entity(entity, new_pt), False)


def blob_to_vein(world, entity, vein):
   entity_pt = entity.get_position()
   if not vein:
      return ([entity_pt], False)
   vein_pt = vein.get_position()
   if entity_pt.adjacent_to(vein_pt):
      remove_entity(world, vein)
      return ([vein_pt], True)
   else:
      new_pt = blob_next_position(world, entity_pt, vein_pt)
      old_entity = world.get_tile_occupant(new_pt)
      if isinstance(old_entity, entities.Ore):
         remove_entity(world, old_entity)
      return (world.move_entity(entity, new_pt), False)


def find_open_around(world, pt, distance):
   for dy in range(-distance, distance + 1):
      for dx in range(-distance, distance + 1):
         new_pt = point.Point(pt.x + dx, pt.y + dy)

         if (world.within_bounds(new_pt) and
            (not world.is_occupied(new_pt))):
            return new_pt

   return None


def remove_entity(world, entity):
   for action in entity.get_pending_actions():
      world.unschedule_action(action)
   entity.clear_pending_actions(world)
   world.remove_entity(entity)


def create_blob(world, name, pt, rate, ticks, i_store):
   blob = entities.OreBlob(name, pt, rate,
      image_store.get_images(i_store, 'blob'),
      random.randint(BLOB_ANIMATION_MIN, BLOB_ANIMATION_MAX)
      * BLOB_ANIMATION_RATE_SCALE)
   blob.schedule_blob(world, ticks, i_store)
   return blob


def create_ore(world, name, pt, ticks, i_store):
   ore = entities.Ore(name, pt, image_store.get_images(i_store, 'ore'),
      random.randint(ORE_CORRUPT_MIN, ORE_CORRUPT_MAX))
   ore.schedule_ore(world, ticks, i_store)

   return ore


def create_quake(world, pt, ticks, i_store):
   quake = entities.Quake("quake", pt,
      image_store.get_images(i_store, 'quake'), QUAKE_ANIMATION_RATE)
   quake.schedule_quake(world, ticks)
   return quake


def create_vein(world, name, pt, ticks, i_store):
   vein = entities.Vein("vein" + name,
      random.randint(VEIN_RATE_MIN, VEIN_RATE_MAX),
      pt, image_store.get_images(i_store, 'vein'))
   return vein

