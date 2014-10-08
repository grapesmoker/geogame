--  INITIALIZES A HARD-WIRED RING NETWORK
--  clebiere-pkv-antoniojl-dreitter-rglinton
delete from user_graph;
insert into user_graph values (10,4),(4,1),(1,2),(2,7),(7,10),
                              (10,7),(7,2),(2,1),(1,4),(4,10);

--  INITIALIZES ROAD RISKS RANDOMLY
update road set danger=rand()/5;

--  RESETS HISTORY OF ACTIONS AND MESSAGES
delete from trade_offer;
delete from message;
delete from message_graph;
delete from action;

--  RESET GOAL ITEMS
delete from goal_items_per_player;
--  INSERT 4 GOAL ITEMS RANDOMLY FOR EACH PLAYER
insert into goal_items_per_player (player_id, item_type_id) (select id,ceil(rand()*30) from user);
insert into goal_items_per_player (player_id, item_type_id) (select id,ceil(rand()*30) from user);
insert into goal_items_per_player (player_id, item_type_id) (select id,ceil(rand()*30) from user);
insert into goal_items_per_player (player_id, item_type_id) (select id,ceil(rand()*30) from user);

--  RESET ITEMS
delete from item;
--  INSERT RANDOM ITEMS OF ALL KINDS INTO LOCATIONS
insert into item (item_type_id, owner_id, location_id, price) (select id,null,ceil(rand()*40),base_price from item_type);
insert into item (item_type_id, owner_id, location_id, price) (select id,null,ceil(rand()*40),base_price from item_type);
insert into item (item_type_id, owner_id, location_id, price) (select id,null,ceil(rand()*40),base_price from item_type);
-- insert into item (item_type_id, owner_id, location_id, price) (select id,null,ceil(rand()*40),base_price from item_type);
-- insert into item (item_type_id, owner_id, location_id, price) (select id,null,ceil(rand()*40),base_price from item_type);
-- insert into item (item_type_id, owner_id, location_id, price) (select id,null,ceil(rand()*40),base_price from item_type);
-- insert into item (item_type_id, owner_id, location_id, price) (select id,null,ceil(rand()*40),base_price from item_type);
-- insert into item (item_type_id, owner_id, location_id, price) (select id,null,ceil(rand()*40),base_price from item_type);

--  RANDOMLY REMOVE 2/3 OF THE ITEMS (THIS MAINTAINS THE ORDER OF THE RANDOM ITEMS PLACED, ADDING ITEM TYPES UNIFORMLY...
	--  THIS RANDOMIZATION COULD BE PERFORMED BETTER IF THE ORDER OF THE RANDOM ITEM TYPES WAS ALSO RANDOMIZED WHEN INSERTING THEM)
delete from item where id%3<>2;

--  INSERTS THE ITEMS THAT ARE ACTUALLY NEEDED INTO THE WORLD
insert into item (item_type_id, owner_id, location_id, price) (select item_type_id, null, ceil(rand()*40),0 from goal_items_per_player);


--  INITIALIZES THE MONEY FOR EACH PLAYER
update user set score=500;
#update user set score=100000 where id=1;

--  INITIALIZES COMBOS (UNUSED IN VERSION WITH NO COMBOS)
-- delete from combo;
-- insert ignore into combo (combo_type_id,location_id,price) (select id, ceil(rand()*40),base_price from combo_type);
-- insert ignore into combo (combo_type_id,location_id,price) (select id, ceil(rand()*40),base_price from combo_type);
-- insert ignore into combo (combo_type_id,location_id,price) (select id, ceil(rand()*40),base_price from combo_type);
-- insert ignore into combo (combo_type_id,location_id,price) (select id, ceil(rand()*40),base_price from combo_type);
-- insert ignore into combo (combo_type_id,location_id,price) (select id, ceil(rand()*40),base_price from combo_type);
-- delete from combo where id%3=2;

--  PLACES PLAYERS AT RANDOM LOCATIONS
update user set current_location_id = ceil(rand() * 40);

--  INITIALIZES WHAT EACH PLAYER KNOWS AT THE BEGINNING OF THE GAME
--  UNUSED IF ALL PLAYERS KNOW EVERY PART OF THE MAP

--  RESETS MAP KNOWLEDGE
-- delete from location_info_per_player where (player_id,location_id) not in (select id,current_location_id from user);
-- delete from road_info_per_player where (player_id,road_id) not in (select u.id,r.id from user u, road r where r.id in (select r2.id from road r2 where r2.location1_id= u.current_location_id or r2.location2_id = u.current_location_id));

--  INSERTS ADJACENT ROADS TO KNOWLEDGE
-- insert ignore into road_info_per_player (select u.id, r.id from user u, road r where u.current_location_id=r.location1_id);
-- insert ignore into road_info_per_player (select u.id, r.id from user u, road r where u.current_location_id=r.location2_id);

--  INSERTS ADJACENT LOCATIONS TO KNOWLEDGE
-- insert ignore into location_info_per_player (select u.id, r.location1_id from user u, road r, location l where u.current_location_id = l.id and r.location2_id=l.id);
-- insert ignore into location_info_per_player (select u.id, r.location2_id from user u, road r, location l where u.current_location_id = l.id and r.location1_id=l.id);

--  INSERTS CURRENT LOCATION TO KNOWLEDGE
-- insert ignore into location_info_per_player (select u.id, u.current_location_id from user u);