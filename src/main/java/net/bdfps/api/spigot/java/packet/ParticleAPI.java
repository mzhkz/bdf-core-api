/**
 * @author uikota
 * http://perfectfreeze.net:8080/uikota/SplatoonNanoka/blob/master/src/com/github/kotake545/splatoon/Packet/ParticleAPI.java
 */
package net.bdfps.api.spigot.java.packet;


import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class ParticleAPI{
	private static String PackageName;
	private static boolean v1_7 = false;
	private static boolean v1_8 = true;
	static {
		String version = Bukkit.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3];
		PackageName = "net.minecraft.server."+version;
	}
	@SuppressWarnings("deprecation")
	public static void sendAllPlayer(EnumParticle effect, Location location, float xd, float yd, float zd, float speed, int count,boolean j){
		for(Player player:Bukkit.getOnlinePlayers()){
			sendPlayer(player, effect, location, xd, yd, zd, speed, count,j);
		}
	}
	public static boolean sendPlayer(Player player,EnumParticle effect, Location location, float xd, float yd, float zd, float speed, int count,boolean j){
		try {
			sendPacket(player,createEffect(effect, location, xd, yd, zd, speed, count,j));
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public static Object createEffect(EnumParticle effect, Location location, float xd, float yd, float zd, float speed, int amount,boolean j){
		return createEffect(effect,location.getX(),location.getY(),location.getZ(),xd,yd,zd,speed,amount,j);
	}
	public static Object createEffect(String effect, Location location, float xd, float yd, float zd, float speed, int amount,boolean j){
		return createEffect(effect,location.getX(),location.getY(),location.getZ(),xd,yd,zd,speed,amount,j);
	}
	public static Object createEffect(int id, Location location, float xd, float yd, float zd, float speed, int amount,boolean j){
		return createEffect(id,location.getX(),location.getY(),location.getZ(),xd,yd,zd,speed,amount,j);
	}
	public static Object createEffect(Object name,double x, double y, double z, float xd, float yd, float zd, float speed, int amount,boolean j){
		try{
			Object ParticlesPacket = getCraftClass("PacketPlayOutWorldParticles").newInstance();
			EnumParticle par = convert(name);
			if(par.useColor){
				xd=getColor(par.color[0]);
				yd=getColor(par.color[1]);
				zd=getColor(par.color[2]);
				speed = 1.0F;
				amount = 0;
			}
			for (Field field : ParticlesPacket.getClass().getDeclaredFields()){
				field.setAccessible(true);
				String fieldName = field.getName();
				if (fieldName.equals("a")) {
					field.set(ParticlesPacket, getEnumParticle(par));
				} else if (fieldName.equals("b")) {
					field.setFloat(ParticlesPacket,(float) x);
				} else if (fieldName.equals("c")) {
					field.setFloat(ParticlesPacket, (float) y);
				} else if (fieldName.equals("d")) {
					field.setFloat(ParticlesPacket,(float) z);
				} else if (fieldName.equals("e")) {
					field.setFloat(ParticlesPacket, xd);
				} else if (fieldName.equals("f")) {
					field.setFloat(ParticlesPacket, yd);
				} else if (fieldName.equals("g")) {
					field.setFloat(ParticlesPacket, zd);
				} else if (fieldName.equals("h")) {
					field.setFloat(ParticlesPacket, speed);
				} else if (fieldName.equals("i")) {
					field.setInt(ParticlesPacket, amount);
				} else if (fieldName.equals("j")) { //since vkotake
					field.setBoolean(ParticlesPacket,j);
				} else if (fieldName.equals("k")) { //since v1.8
					field.set(ParticlesPacket, par.data);
				}
			}
			return ParticlesPacket;
		}catch(Throwable ex){
			return null;
		}
	}
	protected static float getColor(float value) {
		if (value <= 0.0F) {
			value = -1.0F;
		}
		return value / 255.0F;
	}
	private static EnumParticle convert(Object obj) throws Exception{
		EnumParticle par = null;
		if(obj instanceof EnumParticle){
			par = (EnumParticle)obj;
		}else if(obj instanceof String){
			par = EnumParticle.getParticle((String)obj);
		}else if(obj instanceof Integer){
			par = EnumParticle.getParticle((Integer)obj);
		}
		if(par != null){
			//1.7 追加されたパーティクルProtocolHackを使っていない場合
			if(v1_7 && par.isNewParticle()) throw new Exception();
			return par;
		}else throw new Exception();
	}
	private static Object getEnumParticle(EnumParticle par) throws Exception{
		if(v1_7){
			//Return String on ver1.7
			return par.getName();
		}else if(v1_8){
			//Return EnumParticle on ver1.8
			int i = par.getID();
			if(EnumParticle.getParticle(i) == null) throw new Exception();
			Class<?> EnumParticleClass = getCraftClass("EnumParticle").getEnumConstants()[0].getClass();
			Method a = EnumParticleClass.getDeclaredMethod("a", int.class);
			Object EnumParticle = a.invoke(EnumParticleClass, i);
			return EnumParticle;
		}else{
			//Unknow version
			throw new Exception();
		}
	}
	private static Class<?> getCraftClass(String s) throws Exception {
		Class<?> craftclass = Class.forName(PackageName+"."+s);
		return craftclass;
	}
	private static void sendPacket(Player p, Object packet) throws Exception{
		Method PlayerHandle = p.getClass().getMethod("getHandle");
		Object EntityPlayer = PlayerHandle.invoke(p);

		Object PlayerConnection = null;
		for(Field field : EntityPlayer.getClass().getDeclaredFields()){
			if(field.getName().equals("playerConnection")){
				PlayerConnection = field.get(EntityPlayer);
				break;
			}

		}
		Method PlayerConnectionMethod = null;
		for(Method m : PlayerConnection.getClass().getDeclaredMethods()){
			if(m.getName().equals("sendPacket")){
				PlayerConnectionMethod = m;
				break;
			}
		}
		PlayerConnectionMethod.invoke(PlayerConnection, packet);
	}

	public enum EnumParticle{
		EXPLOSION("explode",0),
		EXPLOSION_LARGE("largeexplode",1),
		EXPLOSION_HUGE("hugeexplosion",2),
		FIREWORKS_SPARK("fireworksSpark",3),
		WATER_BUBBLE("bubble",4),
		WATER_SPLASH("splash",5),
		WATER_WAKE("wake",6),
		SUSPENDED("suspended",7),
		SUSPENDED_DEPTH("depthsuspend",8),
		CRIT("crit",9),
		CRIT_MAGIC("magicCrit",10),
		SMOKE_NORMAL("smoke",11),
		SMOKE_LARGE("largesmoke",12),
		SPELL("spell",13),
		SPELL_INSTANT("instantSpell",14),
		SPELL_MOB("mobSpell",15),
		SPELL_MOB_AMBIENT("mobSpellAmbient",16),
		SPELL_WITCH("witchMagic",17),
		DRIP_WATER("dripWater",18),
		DRIP_LAVA("dripLava",19),
		VILLAGER_ANGRY("angryVillager",20),
		VILLAGER_HAPPY("happyVillager",21),
		TOWN_AURA("townaura",22),
		NOTE("note",23),
		PORTAL("portal",24),
		ENCHANTMENT_TABLE("enchantmenttable",25),
		FLAME("flame",26),
		LAVA("lava",27),
		FOOTSTEP("footstep",28),
		CLOUD("cloud",29),
		RED_DUST("reddust",30),
		SNOWBALL("snowballpoof",31),
		SNOW_SHOVEL("snowshovel",32),
		SLIME("slime",33),
		HEART("heart",34),
		BARRIER("barrier",35),
		ITEM_CRACK("iconcrack",36),
		BLOCK_CRACK("blockcrack",37),
		BLOCK_DUST("blockdust",38),
		WATER_DROP("droplet",39),
		ITEM_TAKE("take",40),
		MOB_APPEARANCE("mobappearance",41);

		private String name;
		private int id;
		private int[] data;

		private boolean useColor = false;
		private int[] color = new int[]{0,0,0};

		private static Map<String, EnumParticle> X;
		private static Map<Integer, EnumParticle> Y;

		static{
			X = new HashMap<String, EnumParticle>();
			Y = new HashMap<Integer, EnumParticle>();
			for(EnumParticle a : values()){
				X.put(a.getName(), a);
				Y.put(a.getID(), a);
			}
		}
		EnumParticle(String name, int id){
			this.name = name;
			this.id = id;
			if(id == 36){
				data = new int[2];
				data[0] = 256;
				data[1] = 0;
			}else if(id == 37){
				data = new int[2];
				data[0] = 1;
				data[1] = 0;
			}else if(id == 38){
				data = new int[2];
				data[0] = 1;
				data[1] = 0;
			}else data = null;
		}
		public String getName(){
			if(this.equals(ITEM_CRACK)|this.equals(BLOCK_CRACK)|this.equals(BLOCK_DUST)){
				return name+"_"+data[0]+"_"+data[1];
			}
			else return name;
		}
		public int getID(){
			return id;
		}
		public boolean isNewParticle(){
			return (this.equals(BARRIER)|this.equals(BLOCK_CRACK)
					|this.equals(BLOCK_DUST)|this.equals(WATER_DROP)|this.equals(ITEM_TAKE)|this.equals(MOB_APPEARANCE));
		}

		public static EnumParticle getEnumParticle(String typeName){
			String snd = typeName.toUpperCase().replace(" ", "_");
			EnumParticle type = EnumParticle.valueOf(snd);
			return type;
		}
		@SuppressWarnings("deprecation")
		public EnumParticle setItem(ItemStack item){
			int id = item.getTypeId();
			int data = (int)item.getDurability();
			return setItemIDandData(id, data);
		}
		@SuppressWarnings("deprecation")
		public EnumParticle setBlock(Block block){
			int id = block.getTypeId();
			int data = (int)block.getData();
			return setItemIDandData(id, data);
		}
		@SuppressWarnings("deprecation")
		public EnumParticle setMaterial(Material block){
			int id = block.getId();
			return setItemID(id);
		}
		public EnumParticle setColor(Color color){
			return setColor(color.getRed(),color.getGreen(),color.getBlue());
		}
		public EnumParticle setColor(int red,int green,int blue){
			if(this.equals(RED_DUST) || this.equals(FIREWORKS_SPARK)){
				useColor=true;
				color[0]=red;
				color[1]=green;
				color[2]=blue;
				return this;
			}
			return this;//色変えられるのred_dust以外わからない。
		}
		public EnumParticle setItemID(int id){
			return setItemIDandData(id, this.data[1]);
		}
		public EnumParticle setItemData(int data){
			return setItemIDandData(this.data[0], data);
		}
		public EnumParticle setItemIDandData(int id, int data){
			if(this.data != null){
				this.data[0] = id;
				this.data[1] = data;
			}
			return this;
		}

		public static EnumParticle getParticle(String name){
			if(X.containsKey(name)) return X.get(name);
			else return null;
		}
		public static EnumParticle getParticle(int id){
			if(Y.containsKey(id)) return Y.get(id);
			else return null;
		}
	}
}
