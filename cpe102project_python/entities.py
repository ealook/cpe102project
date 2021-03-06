import point
import actions

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

class Entity(object):
   def __init__(self, name, imgs):
      self.name = name
      self.imgs = imgs
      self.current_img = 0

   def get_images(self):
      return self.imgs

   def get_image(self):
      return self.imgs[self.current_img]

   def get_name(self):
      return self.name

   def entity_string(self):
      return 'unknown'

   def next_image(self):
      self.current_img = (self.current_img + 1) % len(self.imgs)

class PositionedEntity(Entity):
   def __init__(self, name, imgs, position):
      self.position = position
      super(PositionedEntity, self).__init__(name, imgs)

   def set_position(self, point):
      self.position = point

   def get_position(self):
      return self.position

class Actionable(object):
   def __init__(self):
      self.pending_actions = []

   def remove_pending_action(self, action):
      self.pending_actions.remove(action)

   def add_pending_action(self, action):
      self.pending_actions.append(action)


   def get_pending_actions(self):
      return self.pending_actions

   def schedule_action(self, world, action, time):
      self.add_pending_action(action)
      world.schedule_action(action, time)

   def clear_pending_actions(self, world):
      for action in self.get_pending_actions():
         world.unschedule_action(action)
      self.pending_actions = []

class Animatable(object):
   def __init__(self, animation_rate):
      self.animation_rate = animation_rate

   def get_animation_rate(self):
      return self.animation_rate

   def schedule_animation(self, world, repeat_count=0):
      self.schedule_action(world,
         self.create_animation_action(world, repeat_count),
         self.get_animation_rate())

   def create_animation_action(self, world, repeat_count):
      def action(current_ticks):
         self.remove_pending_action(action)

         self.next_image()

         if repeat_count != 1:
            self.schedule_action(world,
               self.create_animation_action(world, max(repeat_count - 1, 0)),
               current_ticks + self.get_animation_rate())

         return [self.get_position()]
      return action

class RateControlled(object):
   def __init__(self, rate):
      self.rate = rate

   def get_rate(self):
      return self.rate

class ResourceDriven(object):
   def __init__(self, resource_limit, resource_count):
      self.resource_limit = resource_limit
      self.resource_count = resource_count

   def set_resource_count(self, n):
      self.resource_count = n

   def get_resource_count(self):
      return self.resource_count

   def get_resource_limit(self):
      return self.resource_limit

class Miner(PositionedEntity, Actionable, Animatable, RateControlled, ResourceDriven):
   def __init__(self, name, resource_limit, position, rate, imgs,
      animation_rate, resource_count):
      super(Miner, self).__init__(name, imgs, position)
      Actionable.__init__(self)
      Animatable.__init__(self, animation_rate)
      RateControlled.__init__(self, rate)
      ResourceDriven.__init__(self, resource_limit, resource_count)

   def get_resource_distance(self):
      return self.resource_distance

   def schedule_entity(self, world, i_store):
      self.schedule_miner(world, 0, i_store)

   def schedule_miner(self, world, ticks, i_store):
      self.schedule_action(world, self.create_miner_action(world, i_store),
         ticks + self.get_rate())
      self.schedule_animation(world)

   def try_transform_miner(self, world, transform):
      new_entity = transform(world)
      if self != new_entity:
         self.clear_pending_actions(world)
         world.remove_entity_at(self.get_position())
         world.add_entity(new_entity)
         new_entity.schedule_animation(world)

      return new_entity

   def miner_to_ore(self, world, ore):
      entity_pt = self.get_position()
      if not ore:
         return ([entity_pt], False)
      ore_pt = ore.get_position()
      if entity_pt.adjacent_to(ore_pt):
         self.set_resource_count(1 + self.get_resource_count())
         actions.remove_entity(world, ore)
         return ([ore_pt], True)
      else:
         new_pt = actions.next_position(world, entity_pt, ore_pt)
         return (world.move_entity(self, new_pt), False)


   def miner_to_smith(self, world, smith):
      entity_pt = self.get_position()
      if not smith:
         return ([entity_pt], False)
      smith_pt = smith.get_position()
      if entity_pt.adjacent_to(smith_pt):
         smith.set_resource_count(smith.get_resource_count() +
            self.get_resource_count())
         self.set_resource_count(0)
         return ([], True)
      else:
         new_pt = actions.next_position(world, entity_pt, smith_pt)
         return (world.move_entity(self, new_pt), False)



########## Entities to be used ##########

class Background(Entity):
   def __init__(self, name, imgs):
      super(Background, self).__init__(name, imgs)


class MinerNotFull(Miner):
   def __init__(self, name, resource_limit, position, rate, imgs,
      animation_rate):
      super(MinerNotFull, self).__init__(name, resource_limit, position, rate, imgs,
      animation_rate, 0)

   def entity_string(self):
      return ' '.join(['miner', self.name, str(self.position.x),
         str(self.position.y), str(self.resource_limit),
         str(self.rate), str(self.animation_rate)])

   def create_miner_action(self, world, image_store):
      return self.create_miner_not_full_action(world, image_store)

   def create_miner_not_full_action(self, world, i_store):
      def action(current_ticks):
         self.remove_pending_action(action)

         entity_pt = self.get_position()
         ore = world.find_nearest(entity_pt, Ore)
         (tiles, found) = self.miner_to_ore(world, ore)

         new_entity = self
         if found:
            new_entity = self.try_transform_miner(world,
               self.try_transform_miner_not_full)

         new_entity.schedule_action(world,
            new_entity.create_miner_action(world, i_store),
            current_ticks + new_entity.get_rate())
         return tiles
      return action

   def try_transform_miner_not_full(self, world):
      if self.resource_count < self.resource_limit:
         return self
      else:
         new_entity = MinerFull(
            self.get_name(), self.get_resource_limit(),
            self.get_position(), self.get_rate(),
            self.get_images(), self.get_animation_rate())
         return new_entity

class MinerFull(Miner):
   def __init__(self, name, resource_limit, position, rate, imgs,
      animation_rate):
      super(MinerFull, self).__init__(name, resource_limit, position, rate, imgs,
      animation_rate, resource_limit)

   def create_miner_action(self, world, image_store):
      return self.create_miner_full_action(world, image_store)

   def create_miner_full_action(self, world, i_store):
      def action(current_ticks):
         self.remove_pending_action(action)

         entity_pt = self.get_position()
         smith = world.find_nearest(entity_pt, Blacksmith)
         (tiles, found) = self.miner_to_smith(world, smith)

         new_entity = self
         if found:
            new_entity = self.try_transform_miner(world,
               self.try_transform_miner_full)

         new_entity.schedule_action(world, 
            new_entity.create_miner_action(world, i_store),
            current_ticks + new_entity.get_rate())
         return tiles
      return action

   def try_transform_miner_full(self, world):
      new_entity = MinerNotFull(
         self.get_name(), self.get_resource_limit(),
         self.get_position(), self.get_rate(),
         self.get_images(), self.get_animation_rate())

      return new_entity

class Vein(PositionedEntity, Actionable, RateControlled):
   def __init__(self, name, rate, position, imgs, resource_distance=1):
      self.resource_distance = resource_distance
      super(Vein, self).__init__(name, imgs, position)
      Actionable.__init__(self)
      RateControlled.__init__(self, rate)


   def get_resource_distance(self):
      return self.resource_distance

   def entity_string(self):
      return ' '.join(['vein', self.name, str(self.position.x),
         str(self.position.y), str(self.rate),
         str(self.resource_distance)])

   def schedule_entity(self, world, i_store):
      self.schedule_vein(world, 0, i_store)

   def schedule_vein(self, world, ticks, i_store):
      self.schedule_action(world, self.create_vein_action(world, i_store),
         ticks + self.get_rate())

   def create_vein_action(self, world, i_store):
      def action(current_ticks):
         self.remove_pending_action(action)

         open_pt = actions.find_open_around(world, self.get_position(),
            self.get_resource_distance())
         if open_pt:
            ore = actions.create_ore(world,
               "ore - " + self.get_name() + " - " + str(current_ticks),
               open_pt, current_ticks, i_store)
            world.add_entity(ore)
            tiles = [open_pt]
         else:
            tiles = []

         self.schedule_action(world,
            self.create_vein_action(world, i_store),
            current_ticks + self.get_rate())
         return tiles
      return action

class Ore(PositionedEntity, Actionable, RateControlled):
   def __init__(self, name, position, imgs, rate=5000):
      super(Ore, self).__init__(name, imgs, position)
      Actionable.__init__(self)
      RateControlled.__init__(self, rate)

   def entity_string(self):
      return ' '.join(['ore', self.name, str(self.position.x),
            str(self.position.y), str(self.rate)])

   def schedule_entity(self, world, i_store):
      self.schedule_ore(world, 0, i_store)

   def schedule_ore(self, world, ticks, i_store):
      self.schedule_action(world,
         self.create_ore_transform_action(world, i_store),
         ticks + self.get_rate())

   def create_ore_transform_action(self, world, i_store):
      def action(current_ticks):
         self.remove_pending_action(action)
         blob = actions.create_blob(world, self.get_name() + " -- blob",
            self.get_position(),
            self.get_rate() // BLOB_RATE_SCALE,
            current_ticks, i_store)

         actions.remove_entity(world, self)
         world.add_entity(blob)

         return [blob.get_position()]
      return action

class Blacksmith(PositionedEntity, Actionable, RateControlled, ResourceDriven):
   def __init__(self, name, position, imgs, resource_limit, rate,
      resource_distance=1):
      self.resource_distance = resource_distance
      super(Blacksmith, self).__init__(name, imgs, position)
      Actionable.__init__(self)
      RateControlled.__init__(self, rate)
      ResourceDriven.__init__(self, resource_limit, 0)


   def get_resource_distance(self):
      return self.resource_distance

   def entity_string(self):
      return ' '.join(['blacksmith', self.name, str(self.position.x),
         str(self.position.y), str(self.resource_limit),
         str(self.rate), str(self.resource_distance)])


   def schedule_animation(self, world, repeat_count=0):
      self.schedule_action(world,
         self.create_animation_action(world, repeat_count),
         self.get_animation_rate())

   def schedule_entity(self, world, i_store):
      pass

class Obstacle(PositionedEntity):
   def __init__(self, name, position, imgs):
      super(Obstacle, self).__init__(name, imgs, position)

   def entity_string(self):
      return ' '.join(['obstacle', self.name, str(self.position.x),
         str(self.position.y)])

   def schedule_entity(self, world, i_store):
      pass

class OreBlob(PositionedEntity, Actionable, Animatable, RateControlled):
   def __init__(self, name, position, rate, imgs, animation_rate):
      super(OreBlob, self).__init__(name, imgs, position)
      Actionable.__init__(self)
      Animatable.__init__(self, animation_rate)
      RateControlled.__init__(self, rate)

   def schedule_blob(self, world, ticks, i_store):
      self.schedule_action(world, self.create_ore_blob_action(world, i_store),
         ticks + self.get_rate())
      self.schedule_animation(world)

   def create_ore_blob_action(self, world, i_store):
      def action(current_ticks):
         self.remove_pending_action(action)

         entity_pt = self.get_position()
         vein = world.find_nearest(entity_pt, Vein)
         (tiles, found) = self.blob_to_vein(world, vein)

         next_time = current_ticks + self.get_rate()
         if found:
            quake = actions.create_quake(world, tiles[0], current_ticks, i_store)
            world.add_entity(quake)
            next_time = current_ticks + self.get_rate() * 2

         self.schedule_action(world,
            self.create_ore_blob_action(world, i_store),
            next_time)

         return tiles
      return action

   def blob_to_vein(self, world, vein):
      entity_pt = self.get_position()
      if not vein:
         return ([entity_pt], False)
      vein_pt = vein.get_position()
      if entity_pt.adjacent_to(vein_pt):
         actions.remove_entity(world, vein)
         return ([vein_pt], True)
      else:
         new_pt = actions.blob_next_position(world, entity_pt, vein_pt)
         old_entity = world.get_tile_occupant(new_pt)
         if isinstance(old_entity, Ore):
            actions.remove_entity(world, old_entity)
         return (world.move_entity(self, new_pt), False)

class Quake(PositionedEntity, Actionable, Animatable):
   def __init__(self, name, position, imgs, animation_rate):
      super(Quake, self).__init__(name, imgs, position)
      Actionable.__init__(self)
      Animatable.__init__(self, animation_rate)

   def schedule_quake(self, world, ticks):
      self.schedule_animation(world, QUAKE_STEPS) 
      self.schedule_action(world, self.create_entity_death_action(world),
         ticks + QUAKE_DURATION)

   def create_entity_death_action(self, world):
      def action(current_ticks):
         self.remove_pending_action(action)
         pt = self.get_position()
         actions.remove_entity(world, self)
         return [pt]
      return action