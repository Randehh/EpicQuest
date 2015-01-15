package main.java.randy.epicquest;

import java.util.HashMap;

import main.java.randy.engine.EpicSystem;
import main.java.randy.quests.EpicQuestReward.RewardTypes;
import main.java.randy.quests.EpicQuestTask.TaskTypes;

import org.mcstats.Metrics;
import org.mcstats.Metrics.Graph;

public class MetricsHandler {

	private static Metrics metrics;
	public static int questAmount = 0;
	public static int taskAmount = 0;
	public static HashMap<TaskTypes, Integer> questTypeAmount = new HashMap<TaskTypes, Integer>();
	public static HashMap<RewardTypes, Integer> rewardTypeAmount = new HashMap<RewardTypes, Integer>();

	public static void createGraphs(Metrics metrics){
		MetricsHandler.metrics = metrics;
		setQuestAmount();
		setQuestTypes();
		setTaskAmount();
		setRewardTypes();
		setOptions();
		setTest();
	}

	public static void incrementTaskType(TaskTypes type){
		if(!questTypeAmount.containsKey(type)) questTypeAmount.put(type, 1);
		else questTypeAmount.put(type, questTypeAmount.get(type) + 1);
	}

	public static void incrementRewardType(RewardTypes type){
		if(!rewardTypeAmount.containsKey(type)) rewardTypeAmount.put(type, 1);
		else rewardTypeAmount.put(type, rewardTypeAmount.get(type) + 1);
	}

	public static void Reset(){
		questTypeAmount.clear();
		rewardTypeAmount.clear();
		questAmount = 0;
		taskAmount = 0;
	}

	private static void setTest() {
		Graph graph = metrics.createGraph("Test");
		graph.addPlotter(new Metrics.Plotter("Herp"){
			@Override
			public int getValue() {
				return 4;
			}
		});
		graph.addPlotter(new Metrics.Plotter("Derp"){
			@Override
			public int getValue() {
				return 6;
			}
		});
	}

	private static void setQuestAmount(){
		Graph graph = metrics.createGraph("Average amount of quests");
		int range = 0;
		int interval = 5;
		for(int i = 0; i < questAmount + 1; i++){
			range = interval * i;
			if(questAmount > range && questAmount <= range + interval){
				break;
			}
		}
		graph.addPlotter(new Metrics.Plotter((range + 1) + "-" + (range + interval)){
			@Override
			public int getValue() {
				return 1;
			}
		});
	}

	private static void setQuestTypes() {
		Graph graph = metrics.createGraph("Average amount of tasks per type");
		for(final TaskTypes type : questTypeAmount.keySet()){
			graph.addPlotter(new Metrics.Plotter(type.toString().toLowerCase()){
				@Override
				public int getValue() {
					return questTypeAmount.get(type);
				}
			});
		}
	}

	private static void setTaskAmount() {
		Graph graph = metrics.createGraph("Average amount of tasks");
		int range = 0;
		int interval = 10;
		for(int i = 0; i < taskAmount + 1; i++){
			range = interval * i;
			if(taskAmount > range && taskAmount <= range + interval){
				break;
			}
		}
		graph.addPlotter(new Metrics.Plotter((range + 1) + "-" + (range + interval)){
			@Override
			public int getValue() {
				return 1;
			}
		});
	}

	private static void setRewardTypes() {
		Graph graph = metrics.createGraph("Average amount of rewards per type");
		for(final RewardTypes type : rewardTypeAmount.keySet()){
			graph.addPlotter(new Metrics.Plotter(type.toString().toLowerCase()){
				@Override
				public int getValue() {
					return rewardTypeAmount.get(type);
				}
			});
		}
	}

	private static void setOptions() {

		//Permissions
		Graph graphPermissions = metrics.createGraph("Permissions enabled");
		graphPermissions.addPlotter(new Metrics.Plotter(EpicSystem.usePermissions() ? "True" : "False"){
			@Override
			public int getValue() {
				return 1;
			}
		});
		
		//Quest book
		Graph graphBook = metrics.createGraph("Book enabled");
		graphBook.addPlotter(new Metrics.Plotter(EpicSystem.useBook() ? "True" : "False"){
			@Override
			public int getValue() {
				return 1;
			}
		});

		//Heroes
		Graph graphHeroes = metrics.createGraph("Heroes enabled");
		graphHeroes.addPlotter(new Metrics.Plotter(EpicSystem.useHeroes() ? "True" : "False"){
			@Override
			public int getValue() {
				return 1;
			}
		});

		//Citizens
		Graph graphCitizens = metrics.createGraph("Citizens enabled");
		graphCitizens.addPlotter(new Metrics.Plotter(EpicSystem.useCitizens() ? "True" : "False"){
			@Override
			public int getValue() {
				return 1;
			}
		});
	}
}
