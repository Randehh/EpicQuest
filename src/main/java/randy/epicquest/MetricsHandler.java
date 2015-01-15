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
		
		questAmount = 0;
		taskAmount = 0;
		for(TaskTypes type : TaskTypes.values()){
			if(!questTypeAmount.containsKey(type)){
				questTypeAmount.put(type, 0);
			}
		}
		for(RewardTypes type : RewardTypes.values()) {
			if(!rewardTypeAmount.containsKey(type)){
				rewardTypeAmount.put(type, 0);
			}
		}
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
		graph.addPlotter(new Metrics.Plotter("0"){
            @Override
            public int getValue() {
                    return (questAmount == 0) ? 1 : 0;
            }
		});
		graph.addPlotter(new Metrics.Plotter("1-5"){
            @Override
            public int getValue() {
                    return (questAmount >= 0 && questAmount <= 5) ? 1 : 0;
            }
		});
		graph.addPlotter(new Metrics.Plotter("6-10"){
            @Override
            public int getValue() {
                    return (questAmount >= 6 && questAmount <= 10) ? 1 : 0;
            }
		});
		graph.addPlotter(new Metrics.Plotter("11-15"){
            @Override
            public int getValue() {
                    return (questAmount >= 11 && questAmount <= 15) ? 1 : 0;
            }
		});
		graph.addPlotter(new Metrics.Plotter("16-20"){
            @Override
            public int getValue() {
                    return (questAmount >= 16 && questAmount <= 20) ? 1 : 0;
            }
		});
		graph.addPlotter(new Metrics.Plotter("21-25"){
            @Override
            public int getValue() {
                    return (questAmount >= 21 && questAmount <= 25) ? 1 : 0;
            }
		});
		graph.addPlotter(new Metrics.Plotter("26-30"){
            @Override
            public int getValue() {
                    return (questAmount >= 21 && questAmount <= 25) ? 1 : 0;
            }
		});
		graph.addPlotter(new Metrics.Plotter("31+"){
            @Override
            public int getValue() {
                    return (questAmount >= 31) ? 1 : 0;
            }
		});
	}
	
	private static void setQuestTypes() {
		Graph graph = metrics.createGraph("Average amount of tasks per type");
		for(final TaskTypes type : TaskTypes.values()){
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
		graph.addPlotter(new Metrics.Plotter("0"){
            @Override
            public int getValue() {
                    return (taskAmount == 0) ? 1 : 0;
            }
		});
		graph.addPlotter(new Metrics.Plotter("1-10"){
            @Override
            public int getValue() {
                    return (taskAmount >= 0 && taskAmount <= 10) ? 1 : 0;
            }
		});
		graph.addPlotter(new Metrics.Plotter("11-20"){
            @Override
            public int getValue() {
                    return (taskAmount >= 11 && taskAmount <= 20) ? 1 : 0;
            }
		});
		graph.addPlotter(new Metrics.Plotter("21-30"){
            @Override
            public int getValue() {
                    return (taskAmount >= 21 && taskAmount <= 30) ? 1 : 0;
            }
		});
		graph.addPlotter(new Metrics.Plotter("31-40"){
            @Override
            public int getValue() {
                    return (taskAmount >= 31 && taskAmount <= 40) ? 1 : 0;
            }
		});
		graph.addPlotter(new Metrics.Plotter("41-50"){
            @Override
            public int getValue() {
                    return (taskAmount >= 41 && taskAmount <= 50) ? 1 : 0;
            }
		});
		graph.addPlotter(new Metrics.Plotter("51-60"){
            @Override
            public int getValue() {
                    return (taskAmount >= 51 && taskAmount <= 60) ? 1 : 0;
            }
		});
		graph.addPlotter(new Metrics.Plotter("61-70"){
            @Override
            public int getValue() {
                    return (taskAmount >= 61 && taskAmount <= 70) ? 1 : 0;
            }
		});
		graph.addPlotter(new Metrics.Plotter("71-80"){
            @Override
            public int getValue() {
                    return (taskAmount >= 71 && taskAmount <= 80) ? 1 : 0;
            }
		});
		graph.addPlotter(new Metrics.Plotter("81-90"){
            @Override
            public int getValue() {
                    return (taskAmount >= 81 && taskAmount <= 90) ? 1 : 0;
            }
		});
		graph.addPlotter(new Metrics.Plotter("91-100"){
            @Override
            public int getValue() {
                    return (taskAmount >= 91 && taskAmount <= 100) ? 1 : 0;
            }
		});
		graph.addPlotter(new Metrics.Plotter("101+"){
            @Override
            public int getValue() {
                    return (taskAmount >= 101) ? 1 : 0;
            }
		});
	}
	
	private static void setRewardTypes() {
		Graph graph = metrics.createGraph("Average amount of rewards per type");
		for(final RewardTypes type : RewardTypes.values()){
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
		graphPermissions.addPlotter(new Metrics.Plotter("True"){
            @Override
            public int getValue() {
                    return EpicSystem.usePermissions() ? 1 : 0;
            }
		});
		
		graphPermissions.addPlotter(new Metrics.Plotter("False"){
            @Override
            public int getValue() {
                    return EpicSystem.usePermissions() ? 0 : 1;
            }
		});
		
		//Quest book
		Graph graphBook = metrics.createGraph("Book enabled");
		graphBook.addPlotter(new Metrics.Plotter("True"){
            @Override
            public int getValue() {
                    return EpicSystem.useBook() ? 1 : 0;
            }
		});
		
		graphBook.addPlotter(new Metrics.Plotter("False"){
            @Override
            public int getValue() {
                    return EpicSystem.useBook() ? 0 : 1;
            }
		});
		
		//Heroes
		Graph graphHeroes = metrics.createGraph("Heroes enabled");
		graphHeroes.addPlotter(new Metrics.Plotter("True"){
            @Override
            public int getValue() {
                    return EpicSystem.useHeroes() ? 1 : 0;
            }
		});
		
		graphHeroes.addPlotter(new Metrics.Plotter("False"){
            @Override
            public int getValue() {
                    return EpicSystem.useHeroes() ? 0 : 1;
            }
		});
		
		//Citizens
		Graph graphCitizens = metrics.createGraph("Citizens enabled");
		graphCitizens.addPlotter(new Metrics.Plotter("True"){
            @Override
            public int getValue() {
                    return EpicSystem.useCitizens() ? 1 : 0;
            }
		});
		
		graphCitizens.addPlotter(new Metrics.Plotter("False"){
            @Override
            public int getValue() {
                    return EpicSystem.useCitizens() ? 0 : 1;
            }
		});
	}
}
