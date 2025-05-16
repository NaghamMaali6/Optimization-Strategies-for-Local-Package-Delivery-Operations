package application ;

//necessary classes and libraries:
import java.util.* ;
import java.util.stream.Collectors ;

public class Genetic 
{
    //GA PARAMETERS:
    private static final int POPULATION_SIZE = 80 ;  //Population size (Number of candidate solutions in each generation)
    private static final int GENERATIONS = 500 ;  //Total number of generations
    private static final double MUTATION_RATE = 0.05 ;  //Mutation Rate 

    //MAIN GA FUNCTION:
    public static Map<Vehicle , List<Package>> runGeneticAlgorithm(List<Package> allPackages , List<Vehicle> vehicles) 
    {
    	//1) Initialize population with random solutions:
    	List<Map<Vehicle , List<Package>>> population = new ArrayList<>() ;
        for (int i = 0 ; i < POPULATION_SIZE ; i++) 
        {
            population.add(generateRandomSolution(allPackages , vehicles)) ;
        }

        //2) Run evolution loop:
        for (int gen = 0 ; gen < GENERATIONS ; gen++) 
        {
            List<Double> fitness = population.stream().map(solution -> evaluateFitness(solution , vehicles)).collect(Collectors.toList()) ;  //Evaluate fitness of each solution

            //Create new population through selection, crossover, and mutation:
            List<Map<Vehicle , List<Package>>> newPopulation = new ArrayList<>() ;
            while (newPopulation.size() < POPULATION_SIZE) 
            {
                //Select parents:
            	Map<Vehicle , List<Package>> parent1 = tournamentSelection(population , fitness) ;
                Map<Vehicle , List<Package>> parent2 = tournamentSelection(population, fitness) ;
                
                Map<Vehicle , List<Package>> child = crossover(parent1 , parent2 , vehicles) ;  //Create child via crossover

                if (Math.random() < MUTATION_RATE) 
                {
                    mutate(child , vehicles) ;  //Possibly mutate child
                }

                newPopulation.add(child) ;
            }

            population = newPopulation ;  //Replace old population with new one
        }

        //3) Return the best (lowest-cost) solution:
        return population.stream().min(Comparator.comparingDouble(solution -> evaluateFitness(solution , vehicles))).orElse(null) ;
    }

    private static double evaluateFitness(Map<Vehicle , List<Package>> solution , List<Vehicle> vehicles)
    {
        double totalCost = 0 ;

        for (Vehicle v : vehicles)  //Evaluate each vehicle's delivery plan
        {
            List<Package> route = solution.getOrDefault(v , new ArrayList<>()) ;
            double dist = 0 , x = 0 , y = 0 ;

            // 1) Route distance of optimized delivery path from shop:
            for (Package p : route) 
            {
                dist += euclid(x , y , p.getX() , p.getY()) ;
                x = p.getX() ;
                y = p.getY() ; 
            }
            dist += euclid(x , y , 0 , 0) ; //Return to shop
            totalCost += dist ;

            // 2) Priority‐late penalty (each step multiplied by its package priority):
            double priPen = 0 ;
            for (int i = 0 ; i < route.size() ; i++)
            {
                priPen += route.get(i).getPriority() * i ;
            }
            totalCost += priPen * 0.1 ;

            // 3) Load balance penalty (how far vehicle is from full utilization):
            double load = route.stream().mapToDouble(Package::getWeight).sum() ; 
            totalCost += Math.abs(v.getCapacity() - load) * 0.05 ;
        }
        
        return totalCost ;
    }

    private static double euclid(double x1 , double y1 , double x2 , double y2)  //a method Calculates the Euclidean distance between two points
    {
        return Math.hypot(x1 - x2 , y1 - y2) ;  //the hypot() method returns the length of hypotenuse of a right angle triangle which is equal to the distance between 2D point(x,y) and the origin (= Math.sqrt(x*x + y*y))
    }

    private static Map<Vehicle , List<Package>> generateRandomSolution(List<Package> packages , List<Vehicle> vehicles)  //random solution generator method
    {
        Map<Vehicle, List<Package>> solution = new LinkedHashMap<>() ;
        for (Vehicle v : vehicles) 
        {
            solution.put(v , new ArrayList<>()) ;
        }

        //Shuffle packages randomly:
        List<Package> shuffled = new ArrayList<>(packages) ;
        Collections.shuffle(shuffled) ;
        
        //assigning each package to a vehicle within capacity:
        for (Package p : shuffled) 
        {
            boolean assigned = false ;
            for (Vehicle v : vehicles) 
            {
                double currentLoad = solution.get(v).stream().mapToDouble(Package::getWeight).sum() ;
                if (currentLoad + p.getWeight() <= v.getCapacity()) 
                {
                    solution.get(v).add(p) ;
                    assigned = true ;
                    break ;
                }
            }
            
            if (!assigned) 
            {
                //If no vehicle has space, assign to the least loaded one:
            	Vehicle leastLoaded = vehicles.stream().min(Comparator.comparingDouble(v -> solution.get(v).stream().mapToDouble(Package::getWeight).sum())).orElse(vehicles.get(0)) ;
                solution.get(leastLoaded).add(p) ;
            }
        }

        return solution ;
    }

    private static Map<Vehicle , List<Package>> tournamentSelection(List<Map<Vehicle , List<Package>>> population , List<Double> fitness)  //Tournament selection method 
    {
        int k = 3 ;  //Tournament size
        Random rand = new Random() ;
        Map<Vehicle , List<Package>> best = null ;
        double bestFit = Double.MAX_VALUE ;

        //Randomly pick k individuals and select the one with best (lowest) fitness:
        for (int i = 0 ; i < k ; i++) 
        {
            int idx = rand.nextInt(population.size()) ;
            double fit = fitness.get(idx) ;
            if (fit < bestFit) 
            {
                bestFit = fit ;
                best = population.get(idx) ;
            }
        }

        return deepCopy(best) ;  //Return a copy to avoid overwriting original
    }

    private static Map<Vehicle , List<Package>> crossover(Map<Vehicle , List<Package>> parent1 , Map<Vehicle , List<Package>> parent2 , List<Vehicle> vehicles)  //crossover method
    {
        Map<Vehicle , List<Package>> child = new LinkedHashMap<>() ;

        // 1) Gather all packages from both parents:
        Set<Package> allPackages = new HashSet<>() ;
        for (Vehicle v : vehicles) 
        {
            allPackages.addAll(parent1.getOrDefault(v , new ArrayList<>())) ;
            allPackages.addAll(parent2.getOrDefault(v , new ArrayList<>())) ;
            child.put(v , new ArrayList<>()) ;
        }

        // 2) Randomize package order for child assignment:
        List<Package> shuffledPackages = new ArrayList<>(allPackages) ;
        Collections.shuffle(shuffledPackages) ;

        // 3) Assign packages to vehicles in the child:
        for (Package p : shuffledPackages) 
        {
            boolean assigned = false ;
            for (Vehicle v : vehicles)
            {
                List<Package> list = child.get(v) ;
                double currentLoad = list.stream().mapToDouble(Package::getWeight).sum() ;
                if (currentLoad + p.getWeight() <= v.getCapacity()) 
                {
                    list.add(p) ;
                    assigned = true ;
                    break ;
                }
            }
            
            if (!assigned) 
            {
                //If no vehicle has space, assign to the least loaded one:
            	Vehicle fallback = vehicles.stream().min(Comparator.comparingDouble(v -> child.get(v).stream().mapToDouble(Package::getWeight).sum())).orElse(vehicles.get(0)) ;
                child.get(fallback).add(p) ; 
            }
        }

        return child ;
    }

    private static void mutate(Map<Vehicle , List<Package>> solution , List<Vehicle> vehicles)  //mutation method 
    {
        //Flatten all assigned packages into one list:
    	List<Package> allPackages = new ArrayList<>() ;
        for (Vehicle v : vehicles) 
        {
            allPackages.addAll(solution.getOrDefault(v, new ArrayList<>())) ;
        }
        
        //Reassign packages randomly:
        Collections.shuffle(allPackages) ;
        solution.clear() ;
        for (Vehicle v : vehicles) 
        {
            solution.put(v , new ArrayList<>()) ;
        }
        for (Package p : allPackages) 
        {
            boolean assigned = false ;
            for (Vehicle v : vehicles)
            {
                double currentLoad = solution.get(v).stream().mapToDouble(Package::getWeight).sum() ;
                if (currentLoad + p.getWeight() <= v.getCapacity()) 
                {
                    solution.get(v).add(p) ;
                    assigned = true ;
                    break ;
                }
            }
            
            if (!assigned) 
            {
            	//If no vehicle has space, assign to the least loaded one:
            	Vehicle fallback = vehicles.stream().min(Comparator.comparingDouble(v -> solution.get(v).stream().mapToDouble(Package::getWeight).sum())).orElse(vehicles.get(0)) ;
                solution.get(fallback).add(p) ; 
            }
        }
    }

    private static Map<Vehicle , List<Package>> deepCopy(Map<Vehicle , List<Package>> original) 
    {
    	//Deep copy of the current solution map with new lists but same objects:
    	Map<Vehicle , List<Package>> copy = new LinkedHashMap<>() ;
        for (Map.Entry<Vehicle , List<Package>> entry : original.entrySet()) 
        {
            copy.put(entry.getKey(), new ArrayList<>(entry.getValue())) ;  //shallow copy of packages 
        }
        return copy ;
    }
    
    public static void printSolution(Map<Vehicle , List<Package>> sol)  //method to print the final optimized solution route, vehicle info, and assigned packages clearly to the conole to test the algorithm 
    {
        for (Vehicle v : sol.keySet())
        {
            double load = sol.get(v).stream().mapToDouble(Package::getWeight).sum() ;
            System.out.println("Vehicle ID: " + v.getID()) ;
            System.out.println(" Capacity: " + v.getCapacity() + "kg") ;
            System.out.println(" Current Load: " + load + "kg") ;

            List<Package> pkgs = sol.get(v) ;
            if (pkgs.isEmpty())
            {
                System.out.println(" Assigned Packages: (none—stays at shop)") ;
            } 
            else 
            {
                System.out.println(" Assigned Packages:") ;
                for (Package p : pkgs) 
                {
                    System.out.printf("  - Package %d | (%.1f,%.1f) | %.1fkg | prio %d\n" , p.getID() , p.getX() , p.getY() , p.getWeight() , p.getPriority()) ;
                }
                
                System.out.print(" Delivery Path: Shop(0,0)") ;
                for (Package p : pkgs) 
                {
                    System.out.printf(" -> P%d(%.1f,%.1f)" , p.getID() , p.getX() , p.getY()) ;
                }
                System.out.println(" -> Shop(0,0)") ;
            }
            System.out.println("-----------------------------------------------------") ;
        }
    }
}