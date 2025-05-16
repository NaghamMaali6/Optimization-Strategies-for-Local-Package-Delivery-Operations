package application ;

//necessary classes and libraries:
import java.util.* ;

/*
 * This class implements the Simulated Annealing algorithm to assign packages to vehicles in a cost-effective way. 
 * It attempts to minimize delivery route distance,
 * balance load among vehicles, and deliver high-priority packages earlier.
 */

public class SimulatedAnnealing 
{
	public static Map<Vehicle , List<Package>> optimize(List<Vehicle> vehicles , List<Package> packages)  //Main method to perform the optimization using Simulated Annealing.
    {
        //SAA PARAMETERS:
		double temperature = 1000 ;               //Initial Temperature
        double coolingRate = 0.95 ;               //rate at which the temperature cools down each step(Between 0.90 and 0.99)
        int iterationsPerTemperature = 100 ;      //how many solutions to test at each temperature level

        // 1) Initial state (greedy + priority sort):
        Map<Vehicle , List<Package>> current = generateInitialSolution(vehicles , packages) ;
        double currentCost = evaluateSolution(current) ;

        // 2) Best-so-far (initialize best solution as current):
        Map<Vehicle , List<Package>> best = deepCopy(current) ;
        double bestCost = currentCost ;

        // 3) Main Annealing loop:
        while (temperature > 1) 
        {
            for (int i = 0 ; i < iterationsPerTemperature ; i++) 
            {
                //Generate a neighboring solution by modifying the current one slightly
            	Map<Vehicle , List<Package>> candidate = generateNeighbor(deepCopy(current), vehicles);
                double candCost = evaluateSolution(candidate);

                // ΔE = currentCost - candCost
                // Accept if better, else with probability e^(ΔE / T) if worse:
                if (acceptanceProbability(currentCost , candCost , temperature) > Math.random())
                {
                    current = candidate ;
                    currentCost = candCost ;

                    //ipdate the best solution if that's the best so far:
                    if (candCost < bestCost)
                    {
                        best = deepCopy(candidate) ;
                        bestCost = candCost ;
                    }
                }
            }
            // 4) Cool down (reduce the temperature for the next cycle):
            temperature *= coolingRate ;
        }

        return best ;  //A map where each vehicle is mapped to a list of packages it should deliver
    }

    
    private static Map<Vehicle , List<Package>> generateInitialSolution(List<Vehicle> vehicles , List<Package> packages) {
        Map<Vehicle , List<Package>> sol = new LinkedHashMap<>() ;  //Preserve insertion order of vehicles:
        for (Vehicle v : vehicles) 
        {
            sol.put(v , new ArrayList<>()) ;
        }

        //Sort by priority (1 high) then by weight (lower weight first):
        packages.sort(Comparator.comparingInt(Package::getPriority).thenComparingDouble(Package::getWeight)) ;

        //Assign packages to vehicles using a greedy fit:
        for (Package p : packages) 
        {
            Vehicle best = null ;
            double bestLoadAfter = Double.MAX_VALUE ;
            
            for (Vehicle v : vehicles) 
            {
                double load = sol.get(v).stream().mapToDouble(Package::getWeight).sum() ;
                if (load + p.getWeight() <= v.getCapacity() && load + p.getWeight() < bestLoadAfter)  //Try to assign each to the vehicle with least leftover capacity
                {

                    best = v ;
                    bestLoadAfter = load + p.getWeight() ;
                }
            }
            
            //Assign the package to the selected vehicle if possible:
            if (best != null) 
            {
                sol.get(best).add(p) ;
            }
        }
        return sol ;
    }
    

    /*
     * generateNeighbor methed Randomly chooses and applies one of three neighborhood mutation strategies:
     *  - Move a package to another vehicle
     *  - Swap two packages between vehicles
     *  - Shuffle delivery order of one vehicle
     */
    private static Map<Vehicle , List<Package>> generateNeighbor(Map<Vehicle , List<Package>> sol , List<Vehicle> vehicles) 
    {
        switch (new Random().nextInt(3))  //Randomly pick one of three neighbor strategies 
        {
            case 0: return movePackage(sol , vehicles) ;
            case 1: return swapPackages(sol , vehicles) ;
            case 2: return shuffleDeliveryOrder(sol , vehicles) ;
            default: return sol ;
        }
    }

    private static Map<Vehicle , List<Package>> movePackage(Map<Vehicle , List<Package>> sol , List<Vehicle> vehicles)  //a method Moves a randomly selected package from one vehicle to another if the capacity allows 
    {
        //Move one random package from one vehicle to another:
    	Random r = new Random() ;
        Vehicle from = vehicles.get(r.nextInt(vehicles.size())) ;
        Vehicle to   = vehicles.get(r.nextInt(vehicles.size())) ;
        List<Package> src = sol.get(from) ;
        
        //prevent invalid move:
        if (from == to || src.isEmpty())
        {
        	return sol ;
        }

        int idx = r.nextInt(src.size()) ;
        Package p = src.get(idx) ;

        double toLoad = sol.get(to).stream().mapToDouble(Package::getWeight).sum() ;
        if (toLoad + p.getWeight() <= to.getCapacity()) 
        {
            src.remove(idx) ;
            sol.get(to).add(p) ;
        }
        return sol ;
    }

    private static Map<Vehicle , List<Package>> swapPackages(Map<Vehicle , List<Package>> sol , List<Vehicle> vehicles)  //a method Swaps one package from two different vehicles if both capacities remain valid
    {
        //Swap one package from each of two different vehicles:
    	Random r = new Random() ;
        Vehicle v1 = vehicles.get(r.nextInt(vehicles.size())) ;
        Vehicle v2 = vehicles.get(r.nextInt(vehicles.size())) ;
        if (v1 == v2) return sol ;

        List<Package> l1 = sol.get(v1) , l2 = sol.get(v2) ;
        if (l1.isEmpty() || l2.isEmpty()) return sol ;

        Package p1 = l1.get(r.nextInt(l1.size())) ;
        Package p2 = l2.get(r.nextInt(l2.size())) ;
        double load1 = l1.stream().mapToDouble(Package::getWeight).sum() - p1.getWeight() + p2.getWeight() ;
        double load2 = l2.stream().mapToDouble(Package::getWeight).sum() - p2.getWeight() + p1.getWeight() ;

        if (load1 <= v1.getCapacity() && load2 <= v2.getCapacity()) 
        {
            l1.remove(p1); 
            l2.remove(p2) ;
            l1.add(p2) ;   
            l2.add(p1) ;
        }
        return sol ;
    }

    /*
     * shuffleDeliveryOrder method Randomly shuffles the delivery order for a vehicle.
     * This affects delivery route (distance and priority-late penalty).
     */
    private static Map<Vehicle , List<Package>> shuffleDeliveryOrder(Map<Vehicle , List<Package>> sol , List<Vehicle> vehicles) 
    {
        //Randomly reorder the delivery sequence within one vehicle:
    	Vehicle v = vehicles.get(new Random().nextInt(vehicles.size())) ;
        List<Package> list = sol.get(v) ;
        if (list.size() > 1) 
        {
        	Collections.shuffle(list) ;
        }
        return sol ;
    }

    /*
     * method evaluateSolution Calculates the total cost of the current solution.
     * The cost is composed of:
     *  - Total travel distance of all vehicles
     *  - Penalty for delivering high-priority packages late
     *  - Penalty for under-utilized vehicle capacity
     */
    private static double evaluateSolution(Map<Vehicle , List<Package>> sol) 
    {
        double cost = 0 ;
        for (Map.Entry<Vehicle , List<Package>> e : sol.entrySet()) 
        {
            Vehicle v = e.getKey() ;
            List<Package> assigned = e.getValue() ;
            
            //Cost = total route distance + priority penalty + load balance penalty:
            
            // 1) Route distance of optimized delivery path from shop:
            List<Package> route = optimizeRouteFromShop(assigned) ;
            double dist = 0 , x = 0 , y = 0 ;
            for (Package p : route) 
            {
                dist += euclid(x , y , p.getX() , p.getY()) ;
                x = p.getX() ;
                y = p.getY() ;
            }
            dist += euclid(x , y , 0 , 0) ;  //return to shop
            cost += dist ;

            // 2) Priority‐late penalty (each step multiplied by its package priority):
            double priPen = 0 ;
            for (int i = 0 ; i < route.size() ; i++) 
            {
                priPen += route.get(i).getPriority() * i ;
            }
            cost += priPen * 0.1 ;

            // 3) Load balance penalty (how far vehicle is from full utilization):
            double load = sol.get(v).stream().mapToDouble(Package::getWeight).sum() ;
            cost += Math.abs(v.getCapacity() - load) * 0.05 ;
        }
        return cost ;
    }

    /*
     * method optimizeRouteFromShop Uses a nearest-neighbor approach to build the delivery route from the shop (0,0).
     * This improves efficiency for the distance calculation.
     */
    public static List<Package> optimizeRouteFromShop(List<Package> pkgs) 
    {
        Set<Package> rem = new HashSet<>(pkgs) ;
        List<Package> route = new ArrayList<>() ;
        double x = 0 , y = 0 ;

        while (!rem.isEmpty()) 
        {
            //Nearest‐neighbor greedy route from (0,0)
        	Package best = null ;
            double bestD = Double.MAX_VALUE ;
            for (Package p : rem) 
            {
                double d = euclid(x , y , p.getX() , p.getY()) ;
                if (d < bestD) 
                {
                    bestD = d ;
                    best = p ;
                }
            }
            route.add(best) ;
            rem.remove(best) ;
            x = best.getX() ; 
            y = best.getY() ;
        }
        return route ;
    }

    private static double euclid(double x1 , double y1 , double x2 , double y2)  //a method Calculates the Euclidean distance between two points
    {
        return Math.hypot(x1 - x2 , y1 - y2) ;  //the hypot() method returns the length of hypotenuse of a right angle triangle which is equal to the distance between 2D point(x,y) and the origin (= Math.sqrt(x*x + y*y))
    }

    private static double acceptanceProbability(double currCost , double newCost , double T)  //a method to calculate the Probability of accepting a worse solution to help the algorithm escape local minima
    {
        if (newCost < currCost) return 1.0 ;
        return Math.exp((currCost - newCost) / T) ;
    }

    private static Map<Vehicle , List<Package>> deepCopy(Map<Vehicle , List<Package>> orig) 
    {
        //Deep copy of the current solution map with new lists but same objects:
    	Map<Vehicle,List<Package>> copy = new LinkedHashMap<>() ;
        for (Map.Entry<Vehicle , List<Package>> e : orig.entrySet()) 
        {
            copy.put(e.getKey() , new ArrayList<>(e.getValue())) ;  //shallow copy of packages
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
                
                //Print route/path:
                List<Package> route = optimizeRouteFromShop(pkgs) ;
                System.out.print(" Delivery Path: Shop(0,0)") ;
                for (Package p : route) 
                {
                    System.out.printf(" -> P%d(%.1f,%.1f)" , p.getID() , p.getX() , p.getY()) ;
                }
                System.out.println(" -> Shop(0,0)") ;
            }
            System.out.println("-----------------------------------------------------") ;
        }
    }
}
