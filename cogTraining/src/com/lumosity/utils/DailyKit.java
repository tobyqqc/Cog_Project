package com.lumosity.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class DailyKit {
	
	public static void main(String[] args) {
//		算法的输入数据：当前用户在所有游戏中的最新得分；
//		当前用户的训练偏好和权重（每个能力的权重）；
//		游戏配置表；
//		相斥性矩阵
//		能力的排列顺序为 速度 记忆 注意 灵活 逻辑
//		game_datas格式 每一行为一个游戏，游戏的数据：），

//		float[][] game_datas={{878,1,0,5,0,1,1},{1760,1,3,0,1,1,0},{718,1,6,5,1,0,1},{350,1,7,0,1,1,0},{1422,1,4,5,1,1,0},{555,1,5,5,1,1,0},{1009,1,6,5,1,1,0},{266,1,8,5,1,1,0},{1962,1,8,5,1,1,0},{311,2,3,5,1,1,1},{978,2,0,5,1,1,0},{747,2,0,5,0,0,0},{1963,2,3,5,1,1,0},{871,2,6,5,1,1,0},{418,3,9,5,1,1,0},{64,3,23,5,1,1,0},{1165,3,11,5,1,1,0},{1159,3,8,5,1,1,0},{1006,3,9,5,1,1,0},{547,4,3,5,1,1,0},{1221,4,6,5,1,1,0},{1735,4,5,5,1,1,0},{165,4,5,5,1,1,0},{1752,4,4,5,1,1,0},{901,5,3,5,1,1,0},{577,5,2,5,1,1,0},{1350,5,1,5,1,1,0},{175,5,0,5,1,1,0},{1559,5,14,5,1,1,0},{799,5,13,5,1,1,0}};
		// 需要从数据库取得
		// 0: 最后一次分数 
		// 1: 类型 1.速度 2.记忆 3.注意 4.灵活 5.逻辑
		// 2.session introduction num，保存在数据库
		// 3.已经玩了的次数， count(game_history)  userid and gameId
		// 4.5.6.之前三次是否推荐了这个游戏 1:推荐  0:未推荐
		float[][] game_datas={{0,2,0,0,0,0,0},{0,2,2,0,0,0,0},{0,2,3,0,0,0,0},{0,2,4,0,0,0,0},{0,2,1,0,0,0,0},{0,3,0,0,0,0,0},{0,3,1,0,0,0,0},{0,1,0,0,0,0,0},{0,1,3,0,0,0,0},{0,1,1,0,0,0,0},{0,1,2,0,0,0,0},{0,5,0,0,0,0,0},{0,5,1,0,0,0,0},{0,4,0,0,0,0,0},{0,4,3,0,0,0,0},{0,4,2,0,0,0,0},{0,4,1,0,0,0,0}};
//		float[][] game_datas={{1153,4,0,79,1,1,0},{1428,2,8,22,1,1,0},{1536,4,13,25,0,1,1},{1607,2,12,3,0,0,1},{1266,2,6,30,0,0,1},{1502,1,2,41,1,1,0},{1595,5,5,93,1,0,1},{1598,4,12,37,1,1,1},{1483,1,10,95,0,0,0},{1312,4,15,62,1,0,1},{1024,1,4,62,1,1,1},{1377,5,2,49,1,0,1},{1064,3,8,74,0,0,1},{1622,5,15,89,0,1,1},{1527,1,10,53,1,0,0},{1406,5,3,55,0,1,1},{1544,2,16,8,1,0,1},{1394,4,15,50,0,1,0},{1060,2,2,54,1,1,1},{1059,1,14,76,0,0,0},{1640,2,12,58,0,0,0},{1690,1,12,64,1,0,1},{1574,3,0,72,1,1,1},{1023,4,13,97,1,0,0},{1278,5,13,67,0,0,0},{1448,3,6,49,1,0,0},{1423,4,5,16,0,0,1},{1314,2,10,26,1,0,1},{1022,5,5,1,0,0,0},{1505,1,15,40,1,1,1}};
		
		// 游戏兼容性矩阵 在数据库维护 
		float[][] co_exist={{0,0,0,0,0,1,1,1,1,1,1,1,1,1,1,1,1},{0,0,0,0,0,1,1,1,1,1,1,1,1,1,1,1,1},{0,0,0,0,0,1,1,1,1,1,1,1,1,1,1,1,1},{0,0,0,0,0,1,1,1,1,1,1,1,1,1,1,1,1},{0,0,0,0,0,1,1,1,1,1,1,1,1,1,1,1,1},{1,1,1,1,1,0,0,1,1,1,1,1,1,1,1,1,1},{1,1,1,1,1,0,0,1,1,1,1,1,1,1,1,1,1},{1,1,1,1,1,1,1,0,0,0,0,1,1,1,1,1,1},{1,1,1,1,1,1,1,0,0,0,0,1,1,1,1,1,1},{1,1,1,1,1,1,1,0,0,0,0,1,1,1,1,1,1},{1,1,1,1,1,1,1,0,0,0,0,1,1,1,1,1,1},{1,1,1,1,1,1,1,1,1,1,1,0,0,1,1,1,1},{1,1,1,1,1,1,1,1,1,1,1,0,0,1,1,1,1},{1,1,1,1,1,1,1,1,1,1,1,1,1,0,0,0,0},{1,1,1,1,1,1,1,1,1,1,1,1,1,0,0,0,0},{1,1,1,1,1,1,1,1,1,1,1,1,1,0,0,0,0},{1,1,1,1,1,1,1,1,1,1,1,1,1,0,0,0,0}};
		
		// 
		int now_day=7;//现在是用户在平台上的第几天？ 这个要获取到的
		
		// 可以直接选择，在数据库维护
		float[] standard_pro={0.030612245f,0.040816327f,0.005102041f,0.051020408f,0.025510204f,0.020408163f,0.051020408f,0.015306122f,0.045918367f,0.040816327f,0.030612245f,0.035714286f,0.020408163f,0.045918367f,0.020408163f,0.040816327f,0.040816327f};
		
		// DB问卷数据 userId 
		float[] ability_count={7,6,4,2,1};//每个能力选了几个  这里要从数据库取，或者一开始填好问卷就转换成权重
		float[] ability_weight=get_ability_weight(ability_count);
		float[] probabilitically=null;
		float[] p_step_2=null;
		float[] p_step_3=null;
		float[] p_step_4=null;
		float[] c_step_5=null;
		float[] d_step_6=null;
		float[] p_step_7=null;
		float[] fp=null;
		//看看是不是新用户
		boolean is_new=true;
		for (int i = 0; i < game_datas.length; i++) {
			if (game_datas[i][0]!=0) {
				is_new=false;
				break;
			}
		}
		
		//概率计算 
		//step1
		if (is_new) {
			//新用户 使用常模数据中所有游戏的概率
			System.out.println("this is a new user");
			probabilitically=standard_pro;
			//我这里直接用了常模概率，还没有乘以用户选择的能力的权重
			//常模概率的计算方法 使用常模的分数，经过1，2，3，4，5，6，7，8
		}
		else {
			probabilitically=step_1(game_datas);	
		}
		
		//step2
		p_step_2=step_2(probabilitically, ability_weight,game_datas);
		
		//step3
		p_step_3=step_3(p_step_2);
		
		//step4
		p_step_4=get_ability_weight(p_step_3);//操作方法与get_ability_weight一样的，所以直接用了
				
		//step5
		c_step_5=step_5(p_step_4,now_day,game_datas);
	
		//step6
		d_step_6=step_6(c_step_5,game_datas);
		
		//step7
		p_step_7=step_7(d_step_6,p_step_4);
		
		//step8
		fp=step_8(game_datas,p_step_7);
		
		ArrayList<Integer> game_list=new ArrayList<Integer>();
		//c1——是否有没有玩过的游戏，获取所有的没有玩过的游戏
		//找到最小的被玩0次的 session introduction num
		
		
		
		
		while (game_list.size()<5) {
			int this_game=0;
			boolean has_game_new=false;
			for (int i = 0; i < game_datas.length; i++) {
				if (game_datas[i][3]==0) {
					has_game_new=true;
					break;
				}
			}
			
			int min_new_game=0;
			if (has_game_new) {
				//has new game 
//				System.out.println("new");
				min_new_game=get_min_new_game(game_datas,now_day);
				this_game=min_new_game;
				game_list.add(this_game);
				game_datas[this_game][3]++;
			}else {
				//no new game
//				System.out.println("old");
				this_game=get_max_probalitically(fp);
				game_list.add(this_game);
				game_datas[this_game][3]++;
			}
			probabilitically=update_probabilitically(fp,co_exist,this_game);
			System.out.println(this_game+"-"+game_datas[this_game][1]);
		}
		
		System.out.println("finish");
		
	}
	
	public ArrayList<Integer> findGameIds() {
		return null;
	}

	public static float[] update_probabilitically(float[] arr,float[][] arr_2,int a) {
//		修正概率
		float[] result=new float[arr.length];
		for (int i = 0; i < arr.length; i++) {
			result[i]=arr[i]*arr_2[a][i];
		}
		return result;
	}
	
	public static int get_max_probalitically(float[] arr) {
//		找到概率最大的游戏
		int max_index=0;
		float max_pro=arr[0];
		for (int i = 0; i < arr.length; i++) {
			if (arr[i]>max_pro && arr[i] != 0) {
			 	max_index=i;
			 	max_pro=arr[i];
			}
		}
		return max_index;
	}
	
	public static boolean isnotSameGameClassId(Map<Float, Integer> gameClassMap, float gameClassId) {
		if (gameClassMap.get(gameClassId) != null) {
			return false;
		}
		return true;
	}
	
	public static int get_min_new_game(float[][] arr,int now_day){
//		找到没有玩过的游戏中最小 session introduction num 的游戏
		int t_count=0;
		ArrayList<Integer> new_games_arrlist=new ArrayList<Integer>();
		Map<Float, Integer> gameClassMap = new HashMap<>();
		
		for (int i = 0; i < arr.length; i++) {
			if (arr[i][3]==0 && arr[i][2]<=now_day) {
				if (isnotSameGameClassId(gameClassMap, arr[i][1])) {
					new_games_arrlist.add(i);
					gameClassMap.put(arr[i][1], i);
				}
				else {
					arr[i][3]++;
				}
			}
		}
		
//		System.out.println(new_games_arrlist);
		//new_games_arrlist里存放了所有没有玩过的游戏的index
		ArrayList<Integer> new_games_sin=new ArrayList<Integer>();
		for (int i = 0; i < new_games_arrlist.size(); i++) {
			new_games_sin.add((int) arr[new_games_arrlist.get(i)][2]);
		}
		//new_games_sin里存放了所有新游戏的session introduction num
		int min_session_num=Collections.min(new_games_sin);
//		System.out.println(min_session_num);
		//min_session_num存放了最小的没玩过的 session introduction num
		for (int i = 0; i < new_games_arrlist.size(); i++) {
			if (arr[new_games_arrlist.get(i)][2]==min_session_num) {
				t_count++;
			}
		}
//		为了确保这里肯定为1 ，在做游戏配置表的时候，不允许同一个能力内的两个游戏拥有同样的 session introduction num
		
		for (int i = 0; i < new_games_arrlist.size(); i++) {
			if (arr[new_games_arrlist.get(i)][2]==min_session_num) {
				return new_games_arrlist.get(i);
			}
		}
		return 0;
	}
	
	public static float[] step_8(float[][] game_datas, float[] p_step_7){
//		对概率进行最后的放大或缩小
		float[] result=new float[p_step_7.length];
		for (int i = 0; i < p_step_7.length; i++) {
			if (game_datas[i][3]==0) {
				result[i]=p_step_7[i];
			}else if (game_datas[i][4]==1 && game_datas[i][5]==1 && game_datas[i][6]==1) {
				result[i]=0;
			}else if (game_datas[i][5]==1 && game_datas[i][6]==1) {
				result[i]=p_step_7[i];
			}else if (game_datas[i][5]==1 || game_datas[i][6]==1) {
				result[i]=2*p_step_7[i];
			}else {
				result[i]=p_step_7[i];
			}
		}
		return result;
	}
	public static float[] step_7(float[] arr,float[] arr_2){
//		综合step4的概率和 次数偏差
		float[] result=new float[arr.length];
		for (int i = 0; i < result.length; i++) {
			result[i]=arr[i]*arr_2[i];
		}
		
		result=get_ability_weight(result);
		return result;
	}
	
	public static float[] step_6(float[] arr,float[][] arr_2){
//		第六步 计算偏差
		float[] result=new float[arr_2.length];
		for (int i = 0; i < arr.length; i++) {
			result[i]=arr[i]-arr_2[i][3];
		}
		
		result=standardization(result);
		result=step_3(result);
		return result;
	}
	
 	public static float[] step_5(float[] arr, int now_day, float[][] arr_2){
//		第五步，计算每个游戏的期望推荐次数
		float[] result=new float[arr_2.length];
		float total_game_play=0;
		for (int i = 0; i < arr_2.length; i++) {
			total_game_play+=arr_2[i][3];
		}
		
		for (int i = 0; i < arr_2.length; i++) {
			result[i]=arr[i]*(now_day-arr_2[i][2])*total_game_play/now_day;
		}
		
		return result;
	}
	
	public static float[] step_3(float[] arr){
//		步骤3，对标准化后的分数进行逻辑化处理，但是我认为，也可以直接用 T 公式来做，目的都是为了把可能为负数的标准化分数转换为正数
		float[] tmp_arr=new float[arr.length];
		for (int i = 0; i < arr.length; i++) {
			tmp_arr[i]=(float) (1/(1+1/(Math.pow(Math.E, arr[i]))));
		}
		return tmp_arr;
	}
	
	public static float[] step_2(float[] arr,float[] arr_2,float[][] arr_3){
//		步骤2，每个游戏的概率乘上所属能力的权重
		float[] result=new float[arr.length];
		for (int i = 0; i < arr.length; i++) {
			result[i]=arr[i]*arr_2[(int) arr_3[i][1]-1];
		}
		return result;
	}
	
	public static float[] step_1(float[][] arr){
//		步骤1，计算每个游戏的得分标准化
		float[] tmp_arr = new float[arr.length];
		for (int i = 0; i < tmp_arr.length; i++) {
			tmp_arr[i]=arr[i][0];
		}
		
		float[] new_tmp_arr=standardization(tmp_arr);
		
		return new_tmp_arr;
	}
	
	public static float[] standardization(float[] arr){
//		这个方法用来标准化分数，比如给了十个数，把这十个数转换成对应的标准化分数
		float mean=0;
		float standard_deviation=0;
		float t_sum=0;
		float tt_sum=0;
		for (int i = 0; i < arr.length; i++) {
			t_sum+=arr[i];
		}
		mean=t_sum/arr.length;
		for (int i = 0; i < arr.length; i++) {
			tt_sum+=(arr[i]-mean)*(arr[i]-mean);
		}
		standard_deviation=(float) Math.sqrt(tt_sum/arr.length);
		
		float[] t_result = arr;
		if (standard_deviation==0) {
			for (int i = 0; i < arr.length; i++) {
				t_result[i]=0;
			}
		}
		else {
			for (int i = 0; i < arr.length; i++) {
				t_result[i]=(arr[i]-mean)/standard_deviation;
			}
		}
		
		return t_result;
	}
	
	public static float[] get_ability_weight(float[] arr){
//		这个方法用来计算每个能力的权重 输入为问卷中勾选的每个能力的选项个数
//		7，6，4，2，1
		float t_sum=0;
		float[] t_result=arr;
		for (int i = 0; i < arr.length; i++) {
			t_sum+=arr[i];
		}
		for (int i = 0; i < arr.length; i++) {
			t_result[i]=arr[i]/(t_sum == 0 ? 1 : t_sum);
		}
		return t_result;
	}

	public static float[][] decode_game_score(float[][] arr){
//		这个方法用来解析源数据，得到游戏分数，按照游戏种类划分
		float[][] result=new float[5][];
		int[] cc={0,0,0,0,0};
		int[] tmp_cc={0,0,0,0,0};
		for (int i = 0; i < arr.length; i++) {
			int j=(int) arr[i][1];
			cc[j-1]++;
		}
		for (int i = 0; i < cc.length; i++) {
			result[i]=new float[cc[i]];
		}
		
		for (int i = 0; i < arr.length; i++) {
			int j=(int) arr[i][1];
			result[j-1][tmp_cc[j-1]]=arr[i][0];
			tmp_cc[j-1]++;
		}
		
		return result;
	}
	
	public static float[][] decode_game_session_intro_num(float[][] arr){
//		这个方法用来解析源数据，得到游戏session introduction number，按照游戏种类划分
		float[][] result=new float[5][];
		int[] cc={0,0,0,0,0};
		int[] tmp_cc={0,0,0,0,0};
		for (int i = 0; i < arr.length; i++) {
			int j=(int) arr[i][1];
			cc[j-1]++;
		}
		for (int i = 0; i < cc.length; i++) {
			result[i]=new float[cc[i]];
		}
		
		for (int i = 0; i < arr.length; i++) {
			int j=(int) arr[i][1];
			result[j-1][tmp_cc[j-1]]=arr[i][0];
			tmp_cc[j-1]++;
		}
		
		return result;
	}
	
}
