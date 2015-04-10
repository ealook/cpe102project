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

class Background:
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


class MinerNotFull:
   def __init__(self, name, resource_limit, position, rate, imgs,
      animation_rate):
      self.name = name
      self.position = position
      self.rate = rate
      self.imgs = imgs
      self.current_img = 0
      self.resource_limit = resource_limit
      self.resource_count = 0
      self.animation_rate = animation_rate
      self.pending_actions = []

   def set_position(self, point):
      self.position = point

   def get_position(self):
      return self.position


   def get_images(self):
      return self.imgs

   def get_image(self):
      return self.imgs[self.current_img]


   def get_rate(self):
      return self.rate


   def set_resource_count(self, n):
      self.resource_count = n

   def get_resource_count(self):
      return self.resource_count


   def get_resource_limit(self):
      return self.resource_limit


   def get_resource_distance(self):
      return self.resource_distance


   def get_name(self):
      return self.name


   def get_animation_rate(self):
      return self.animation_rate


   def remove_pending_action(self, action):
      if hasattr(self, "pending_actions"):
         self.pending_actions.remove(action)

   def add_pending_action(self, action):
      if hasattr(self, "pending_actions"):
         self.pending_actions.append(action)


   def get_pending_actions(self):
      if hasattr(self, "pending_actions"):
         return self.pending_actions
      else:
         return []


   def next_image(self):
      self.current_img = (self.current_img + 1) % len(self.imgs)

   def entity_string(self):
      return ' '.join(['miner', self.name, str(self.position.x),
         str(self.position.y), str(self.resource_limit),
         str(self.rate), str(self.animation_rate)])


   def schedule_action(self, world, action, time):
      self.add_pending_action(action)
      world.schedule_action(action, time)


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


   def clear_pending_actions(self, world):
      for action in self.get_pending_actions():
         world.unschedule_action(action)
      if hasattr(self, "pending_actions"):
         self.pending_actions = []

   def schedule_entity(self, world, i_store):
      self.schedule_miner(world, 0, i_store)

   def schedule_miner(self, world, ticks, i_store):
      self.schedule_action(world, self.create_miner_action(world, i_store),
         ticks + self.get_rate())
      self.schedule_animation(world)

   def create_miner_action(self, world, image_store):
      return self.create_miner_not_full_action(world, image_store)

   def create_miner_not_full_action(self, world, i_store):
      def action(current_ticks):
         self.remove_pending_action(action)

         entity_pt = self.get_position()
         ore = world.find_nearest(entity_pt, Ore)
         (tiles, found) = actions.miner_to_ore(world, self, ore)

         new_entity = self
         if found:
            new_entity = self.try_transform_miner(world,
               self.try_transform_miner_not_full)

         new_entity.schedule_action(world,
            new_entity.create_miner_action(world, i_store),
            current_ticks + new_entity.get_rate())
         return tiles
      return action

   def try_transform_miner(self, world, transform):
      new_entity = transform(world)
      if self != new_entity:
         self.clear_pending_actions(world)
         world.remove_entity_at(self.get_position())
         world.add_entity(new_entity)
         new_entity.schedule_animation(world)

      return new_entity

   def try_transform_miner_not_full(self, world):
      if self.resource_count < self.resource_limit:
         return self
      else:
         new_entity = MinerFull(
            self.get_name(), self.get_resource_limit(),
            self.get_position(), self.get_rate(),
            self.get_images(), self.get_animation_rate())
         return new_entity

class MinerFull:
   def __init__(self, name, resource_limit, position, rate, imgs,
      animation_rate):
      self.name = name
      self.position = position
      self.rate = rate
      self.imgs = imgs
      self.current_img = 0
      self.resource_limit = resource_limit
      self.resource_count = resource_limit
      self.animation_rate = animation_rate
      self.pending_actions = []

   def set_position(self, point):
      self.position = point

   def get_position(self):
      return self.position


   def get_images(self):
      return self.imgs

   def get_image(self):
      return self.imgs[self.current_img]


   def get_rate(self):
      return self.rate


   def set_resource_count(self, n):
      self.resource_count = n

   def get_resource_count(self):
      return self.resource_count


   def get_resource_limit(self):
      return self.resource_limit


   def get_resource_distance(self):
      return self.resource_distance


   def get_name(self):
      return self.name


   def get_animation_rate(self):
      return self.animation_rate


   def remove_pending_action(self, action):
      if hasattr(self, "pending_actions"):
         self.pending_actions.remove(action)

   def add_pending_action(self, action):
      if hasattr(self, "pending_actions"):
         self.pending_actions.append(action)


   def get_pending_actions(self):
      if hasattr(self, "pending_actions"):
         return self.pending_actions
      else:
         return []


   def next_image(self):
      self.current_img = (self.current_img + 1) % len(self.imgs)

   def entity_string(self):
      return 'unknown'


   def schedule_action(self, world, action, time):
      self.add_pending_action(action)
      world.schedule_action(action, time)


   def schedule_animation(self, world, repeat_count=0):
      self.schedule_action(world,
         self.create_animation_action(world, repeat_count),
         self.get_animation_rate())


   def clear_pending_actions(self, world):
      for action in self.get_pending_actions():
         world.unschedule_action(action)
      if hasattr(self, "pending_actions"):
         self.pending_actions = []

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

   def create_miner_action(self, world, image_store):
      return self.create_miner_full_action(world, image_store)

   def create_miner_full_action(self, world, i_store):
      def action(current_ticks):
         self.remove_pending_action(action)

         entity_pt = self.get_position()
         smith = world.find_nearest(entity_pt, Blacksmith)
         (tiles, found) = actions.miner_to_smith(world, self, smith)

         new_entity = self
         if found:
            new_entity = self.try_transform_miner(world,
               self.try_transform_miner_full)

         new_entity.schedule_action(world, 
            new_entity.create_miner_action(world, i_store),
            current_ticks + new_entity.get_rate())
         return tiles
      return action

   def try_transform_miner(self, world, transform):
      new_entity = transform(world)
      if self != new_entity:
         self.clear_pending_actions(world)
         world.remove_entity_at(self.get_position())
         world.add_entity(new_entity)
         new_entity.schedule_animation(world)

      return new_entity

   def try_transform_miner_full(self, world):
      new_entity = MinerNotFull(
         self.get_name(), self.get_resource_limit(),
         self.get_position(), self.get_rate(),
         self.get_images(), self.get_animation_rate())

      return new_entity

class Vein:
   def __init__(self, name, rate, position, imgs, resource_distance=1):
      self.name = name
      self.position = position
      self.rate = rate
      self.imgs = imgs
      self.current_img = 0
      self.resource_distance = resource_distance
      self.pending_actions = []

   def set_position(self, point):
      self.position = point

   def get_position(self):
      return self.position


   def get_images(self):
      return self.imgs

   def get_image(self):
      return self.imgs[self.current_img]


   def get_rate(self):
      return self.rate


   def get_resource_distance(self):
      return self.resource_distance


   def get_name(self):
      return self.name


   def remove_pending_action(self, action):
      if hasattr(self, "pending_actions"):
         self.pending_actions.remove(action)

   def add_pending_action(self, action):
      if hasattr(self, "pending_actions"):
         self.pending_actions.append(action)


   def get_pending_actions(self):
      if hasattr(self, "pending_actions"):
         return self.pending_actions
      else:
         return []


   def next_image(self):
      self.current_img = (self.current_img + 1) % len(self.imgs)

   def entity_string(self):
      return ' '.join(['vein', self.name, str(self.position.x),
         str(self.position.y), str(self.rate),
         str(self.resource_distance)])


   def schedule_action(self, world, action, time):
      self.add_pending_action(action)
      world.schedule_action(action, time)


   def schedule_animation(self, world, repeat_count=0):
      self.schedule_action(world,
         self.create_animation_action(world, repeat_count),
         self.get_animation_rate())


   def clear_pending_actions(self, world):
      for action in self.get_pending_actions():
         world.unschedule_action(action)
      if hasattr(self, "pending_actions"):
         self.pending_actions = []

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

class Ore:
   def __init__(self, name, position, imgs, rate=5000):
      self.name = name
      self.position = position
      self.imgs = imgs
      self.current_img = 0
      self.rate = rate
      self.pending_actions = []

   def set_position(self, point):
      self.position = point

   def get_position(self):
      return self.position


   def get_images(self):
      return self.imgs

   def get_image(self):
      return self.imgs[self.current_img]


   def get_rate(self):
      return self.rate


   def get_name(self):
      return self.name


   def remove_pending_action(self, action):
      if hasattr(self, "pending_actions"):
         self.pending_actions.remove(action)

   def add_pending_action(self, action):
      if hasattr(self, "pending_actions"):
         self.pending_actions.append(action)


   def get_pending_actions(self):
      if hasattr(self, "pending_actions"):
         return self.pending_actions
      else:
         return []


   def next_image(self):
      self.current_img = (self.current_img + 1) % len(self.imgs)

   def entity_string(self):
      return ' '.join(['ore', self.name, str(self.position.x),
            str(self.position.y), str(self.rate)])


   def schedule_action(self, world, action, time):
      self.add_pending_action(action)
      world.schedule_action(action, time)


   def schedule_animation(self, world, repeat_count=0):
      self.schedule_action(world,
         self.create_animation_action(world, repeat_count),
         self.get_animation_rate())


   def clear_pending_actions(self, world):
      for action in self.get_pending_actions():
         world.unschedule_action(action)
      if hasattr(self, "pending_actions"):
         self.pending_actions = []


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

class Blacksmith:
   def __init__(self, name, position, imgs, resource_limit, rate,
      resource_distance=1):
      self.name = name
      self.position = position
      self.imgs = imgs
      self.current_img = 0
      self.resource_limit = resource_limit
      self.resource_count = 0
      self.rate = rate
      self.resource_distance = resource_distance
      self.pending_actions = []

   def set_position(self, point):
      self.position = point

   def get_position(self):
      return self.position


   def get_images(self):
      return self.imgs

   def get_image(self):
      return self.imgs[self.current_img]


   def get_rate(self):
      return self.rate


   def set_resource_count(self, n):
      self.resource_count = n

   def get_resource_count(self):
      return self.resource_count


   def get_resource_limit(self):
      return self.resource_limit


   def get_resource_distance(self):
      return self.resource_distance


   def get_name(self):
      return self.name


   def get_animation_rate(self):
      return self.animation_rate


   def remove_pending_action(self, action):
      if hasattr(self, "pending_actions"):
         self.pending_actions.remove(action)

   def add_pending_action(self, action):
      if hasattr(self, "pending_actions"):
         self.pending_actions.append(action)


   def get_pending_actions(self):
      if hasattr(self, "pending_actions"):
         return self.pending_actions
      else:
         return []


   def next_image(self):
      self.current_img = (self.current_img + 1) % len(self.imgs)

   def entity_string(self):
      return ' '.join(['blacksmith', self.name, str(self.position.x),
         str(self.position.y), str(self.resource_limit),
         str(self.rate), str(self.resource_distance)])


   def schedule_action(self, world, action, time):
      self.add_pending_action(action)
      world.schedule_action(action, time)


   def schedule_animation(self, world, repeat_count=0):
      self.schedule_action(world,
         self.create_animation_action(world, repeat_count),
         self.get_animation_rate())


   def clear_pending_actions(self, world):
      for action in self.get_pending_actions():
         world.unschedule_action(action)
      if hasattr(self, "pending_actions"):
         self.pending_actions = []

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

   def schedule_entity(self, world, i_store):
      pass

class Obstacle:
   def __init__(self, name, position, imgs):
      self.name = name
      self.position = position
      self.imgs = imgs
      self.current_img = 0

   def set_position(self, point):
      self.position = point

   def get_position(self):
      return self.position


   def get_images(self):
      return self.imgs

   def get_image(self):
      return self.imgs[self.current_img]


   def get_name(self):
      return self.name

   def next_image(self):
      self.current_img = (self.current_img + 1) % len(self.imgs)

   def entity_string(self):
      return ' '.join(['obstacle', self.name, str(self.position.x),
         str(self.position.y)])

   def schedule_entity(self, world, i_store):
      pass

class OreBlob:
   def __init__(self, name, position, rate, imgs, animation_rate):
      self.name = name
      self.position = position
      self.rate = rate
      self.imgs = imgs
      self.current_img = 0
      self.animation_rate = animation_rate
      self.pending_actions = []

   def set_position(self, point):
      self.position = point

   def get_position(self):
      return self.position


   def get_images(self):
      return self.imgs

   def get_image(self):
      return self.imgs[self.current_img]


   def get_rate(self):
      return self.rate


   def get_name(self):
      return self.name


   def get_animation_rate(self):
      return self.animation_rate


   def remove_pending_action(self, action):
      if hasattr(self, "pending_actions"):
         self.pending_actions.remove(action)

   def add_pending_action(self, action):
      if hasattr(self, "pending_actions"):
         self.pending_actions.append(action)


   def get_pending_actions(self):
      if hasattr(self, "pending_actions"):
         return self.pending_actions
      else:
         return []


   def next_image(self):
      self.current_img = (self.current_img + 1) % len(self.imgs)

   def entity_string(self):
      return 'unknown'


   def schedule_action(self, world, action, time):
      self.add_pending_action(action)
      world.schedule_action(action, time)


   def schedule_animation(self, world, repeat_count=0):
      self.schedule_action(world,
         self.create_animation_action(world, repeat_count),
         self.get_animation_rate())


   def clear_pending_actions(self, world):
      for action in self.get_pending_actions():
         world.unschedule_action(action)
      if hasattr(self, "pending_actions"):
         self.pending_actions = []

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

   def schedule_blob(self, world, ticks, i_store):
      self.schedule_action(world, self.create_ore_blob_action(world, i_store),
         ticks + self.get_rate())
      self.schedule_animation(world)

   def create_ore_blob_action(self, world, i_store):
      def action(current_ticks):
         self.remove_pending_action(action)

         entity_pt = self.get_position()
         vein = world.find_nearest(entity_pt, Vein)
         (tiles, found) = actions.blob_to_vein(world, self, vein)

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

class Quake:
   def __init__(self, name, position, imgs, animation_rate):
      self.name = name
      self.position = position
      self.imgs = imgs
      self.current_img = 0
      self.animation_rate = animation_rate
      self.pending_actions = []

   def set_position(self, point):
      self.position = point

   def get_position(self):
      return self.position


   def get_images(self):
      return self.imgs

   def get_image(self):
      return self.imgs[self.current_img]


   def get_rate(self):
      return self.rate


   def get_name(self):
      return self.name


   def get_animation_rate(self):
      return self.animation_rate


   def remove_pending_action(self, action):
      if hasattr(self, "pending_actions"):
         self.pending_actions.remove(action)

   def add_pending_action(self, action):
      if hasattr(self, "pending_actions"):
         self.pending_actions.append(action)


   def get_pending_actions(self):
      if hasattr(self, "pending_actions"):
         return self.pending_actions
      else:
         return []


   def next_image(self):
      self.current_img = (self.current_img + 1) % len(self.imgs)

   def entity_string(self):
      return 'unknown'


   def schedule_action(self, world, action, time):
      self.add_pending_action(action)
      world.schedule_action(action, time)


   def schedule_animation(self, world, repeat_count=0):
      self.schedule_action(world,
         self.create_animation_action(world, repeat_count),
         self.get_animation_rate())


   def clear_pending_actions(self, world):
      for action in self.get_pending_actions():
         world.unschedule_action(action)
      if hasattr(self, "pending_actions"):
         self.pending_actions = []

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