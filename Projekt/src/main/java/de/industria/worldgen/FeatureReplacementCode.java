package de.industria.worldgen;

public enum FeatureReplacementCode {
	
	ACACIA_TREE("{\"config\":{\"feature\":{\"config\":{\"feature\":{\"config\":{\"features\":[{\"feature\":{\"config\":{\"max_water_depth\":0,\"ignore_vines\":true,\"heightmap\":\"OCEAN_FLOOR\",\"minimum_size\":{\"limit\":1,\"lower_size\":0,\"upper_size\":2,\"type\":\"minecraft:two_layers_feature_size\"},\"decorators\":[],\"trunk_provider\":{\"state\":{\"Properties\":{\"axis\":\"y\"},\"Name\":\"minecraft:acacia_log\"},\"type\":\"minecraft:simple_state_provider\"},\"leaves_provider\":{\"state\":{\"Properties\":{\"persistent\":\"false\",\"distance\":\"7\"},\"Name\":\"minecraft:acacia_leaves\"},\"type\":\"minecraft:simple_state_provider\"},\"foliage_placer\":{\"radius\":2,\"offset\":0,\"type\":\"minecraft:acacia_foliage_placer\"},\"trunk_placer\":{\"base_height\":5,\"height_rand_a\":2,\"height_rand_b\":2,\"type\":\"minecraft:forking_trunk_placer\"}},\"type\":\"minecraft:tree\"},\"chance\":0.8}],\"default\":{\"config\":{\"max_water_depth\":0,\"ignore_vines\":true,\"heightmap\":\"OCEAN_FLOOR\",\"minimum_size\":{\"limit\":1,\"lower_size\":0,\"upper_size\":1,\"type\":\"minecraft:two_layers_feature_size\"},\"decorators\":[],\"trunk_provider\":{\"state\":{\"Properties\":{\"axis\":\"y\"},\"Name\":\"minecraft:oak_log\"},\"type\":\"minecraft:simple_state_provider\"},\"leaves_provider\":{\"state\":{\"Properties\":{\"persistent\":\"false\",\"distance\":\"7\"},\"Name\":\"minecraft:oak_leaves\"},\"type\":\"minecraft:simple_state_provider\"},\"foliage_placer\":{\"radius\":2,\"offset\":0,\"height\":3,\"type\":\"minecraft:blob_foliage_placer\"},\"trunk_placer\":{\"base_height\":4,\"height_rand_a\":2,\"height_rand_b\":0,\"type\":\"minecraft:straight_trunk_placer\"}},\"type\":\"minecraft:tree\"}},\"type\":\"minecraft:random_selector\"},\"decorator\":{\"config\":{\"outer\":{\"config\":{},\"type\":\"minecraft:square\"},\"inner\":{\"config\":{},\"type\":\"minecraft:heightmap\"}},\"type\":\"minecraft:decorated\"}},\"type\":\"minecraft:decorated\"},\"decorator\":{\"config\":{\"count\":1,\"extra_chance\":0.1,\"extra_count\":1},\"type\":\"minecraft:count_extra\"}},\"type\":\"minecraft:decorated\"}"),
	OAK_TREE("{\"config\":{\"feature\":{\"config\":{\"feature\":{\"config\":{\"features\":[{\"feature\":{\"config\":{\"max_water_depth\":0,\"ignore_vines\":true,\"heightmap\":\"OCEAN_FLOOR\",\"minimum_size\":{\"limit\":1,\"lower_size\":0,\"upper_size\":2,\"type\":\"minecraft:two_layers_feature_size\"},\"decorators\":[],\"trunk_provider\":{\"state\":{\"Properties\":{\"axis\":\"y\"},\"Name\":\"minecraft:acacia_log\"},\"type\":\"minecraft:simple_state_provider\"},\"leaves_provider\":{\"state\":{\"Properties\":{\"persistent\":\"false\",\"distance\":\"7\"},\"Name\":\"minecraft:acacia_leaves\"},\"type\":\"minecraft:simple_state_provider\"},\"foliage_placer\":{\"radius\":2,\"offset\":0,\"type\":\"minecraft:acacia_foliage_placer\"},\"trunk_placer\":{\"base_height\":5,\"height_rand_a\":2,\"height_rand_b\":2,\"type\":\"minecraft:forking_trunk_placer\"}},\"type\":\"minecraft:tree\"},\"chance\":0.8}],\"default\":{\"config\":{\"max_water_depth\":0,\"ignore_vines\":true,\"heightmap\":\"OCEAN_FLOOR\",\"minimum_size\":{\"limit\":1,\"lower_size\":0,\"upper_size\":1,\"type\":\"minecraft:two_layers_feature_size\"},\"decorators\":[],\"trunk_provider\":{\"state\":{\"Properties\":{\"axis\":\"y\"},\"Name\":\"minecraft:oak_log\"},\"type\":\"minecraft:simple_state_provider\"},\"leaves_provider\":{\"state\":{\"Properties\":{\"persistent\":\"false\",\"distance\":\"7\"},\"Name\":\"minecraft:oak_leaves\"},\"type\":\"minecraft:simple_state_provider\"},\"foliage_placer\":{\"radius\":2,\"offset\":0,\"height\":3,\"type\":\"minecraft:blob_foliage_placer\"},\"trunk_placer\":{\"base_height\":4,\"height_rand_a\":2,\"height_rand_b\":0,\"type\":\"minecraft:straight_trunk_placer\"}},\"type\":\"minecraft:tree\"}},\"type\":\"minecraft:random_selector\"},\"decorator\":{\"config\":{\"outer\":{\"config\":{},\"type\":\"minecraft:square\"},\"inner\":{\"config\":{},\"type\":\"minecraft:heightmap\"}},\"type\":\"minecraft:decorated\"}},\"type\":\"minecraft:decorated\"},\"decorator\":{\"config\":{\"count\":1,\"extra_chance\":0.1,\"extra_count\":1},\"type\":\"minecraft:count_extra\"}},\"type\":\"minecraft:decorated\"}");
	
	private String code;
	
	private FeatureReplacementCode(String code) {
		this.code = code;
	}
	
	public String getCode() {
		return code;
	}
	
}


//{
//	"config":{
//		"max_water_depth":0,
//		"ignore_vines":true,
//		"heightmap":"OCEAN_FLOOR",
//		"minimum_size":{
//			"limit":1,
//			"lower_size":0,
//			"upper_size":1,
//			"type":"minecraft:two_layers_feature_size"
//		},
//		"decorators":[],
//		"trunk_provider":{
//			"state":{
//				"Properties":{
//					"axis":"y"
//				},
//				"Name":"minecraft:oak_log"
//			},
//			"type":"minecraft:simple_state_provider"
//		},
//		"leaves_provider":{
//			"state":{
//				"Properties":{
//					"persistent":"false",
//					"distance":"7"
//				},
//				"Name":"minecraft:oak_leaves"
//			},
//			"type":"minecraft:simple_state_provider"
//		},
//		"foliage_placer":{
//			"radius":2,
//			"offset":0,
//			"height":3,
//			"type":"minecraft:blob_foliage_placer"
//		},
//		"trunk_placer":{
//			"base_height":4,
//			"height_rand_a":2,
//			"height_rand_b":0,
//			"type":"minecraft:straight_trunk_placer"
//		}
//	},
//	"type":"minecraft:tree"
//}
