# Optimization-Strategies-for-Local-Package-Delivery-Operations
This project focuses on optimizing the daily operations of a small local package delivery shop. Every day, the shop receives multiple packages, each with a destination, a weight, and a priority level (1 to 5, where 1 is the highest priority). The goal is to assign these packages to a limited number of delivery vehicles and plan the delivery routes in a way that reduces the total distance traveled, while also giving preference to delivering high-priority packages earlier.

Each vehicle has a limited carrying capacity, so the assignment must make sure that no vehicle is overloaded. The challenge is to find a good balance between two goals: minimizing the total travel distance (to reduce cost) and delivering high-priority packages as early as possible. This balance is important to ensure both efficiency and quality of service.

To solve this, two optimization algorithms are used: Simulated Annealing and Genetic Algorithms. The program takes input through a graphical interface, where the user provides two files—one containing vehicle information, and the other containing package details. The system then loads the data, processes it, and displays an optimized delivery plan. In short, this project simulates real-world delivery logistics and applies AI techniques to find smart, practical, and balanced solutions for package delivery.

This project was developed using Java along with JavaFX for the graphical user interface. Scene Builder was used to design and organize the interface through .FXML files, allowing for a clean separation between the layout and the logic.
The user interface includes multiple scenes, starting with a Cover Page featuring the app's name "Delivra" and a visually appealing background. The interface uses a consistent color theme of #001D5E (dark blue), #E85E2C (orange), #35E5F3 (cyan), and White, creating a modern and engaging look. Visual elements like images (Cover_Page.png, vehicle.jpg) and styled buttons, labels, and text fields are used to improve user experience.

Users interact with the system by entering the names of two .txt files (one for vehicles and one for packages). A second scene lets users choose between the Simulated Annealing and Genetic Algorithm methods. Once a method is selected, the system computes the optimized routes scene, and the Output Page visualizes the solution dynamically. Horizontal and vertical zoom sliders allow users to explore the canvas in detail. 

To build this system, several Java libraries were used, including:

•	javafx.application.Application

•	javafx.fxml.FXMLLoader

•	javafx.scene.control components like TextField, Button, and Alert

•	javafx.scene.image.Image and ImageView for visuals

•	java.util.* and java.io.* for file handling and data structures

•	javafx.animation.PauseTransition and javafx.util.Duration for handling delays and transitions

•	javafx.scene.canvas.*

In both the Simulated Annealing and Genetic Algorithm implementations, I designed a custom cost function to guide the optimization process toward solutions that balance operational cost with delivery priorities and vehicle load usage. This function is central to maintaining the trade-off between delivering high-priority packages and minimizing total distance traveled.

The total cost of a solution is calculated using three key components:

•	Route Distance – the total kilometers traveled by each vehicle, from the shop to all assigned package destinations and back.

•	Priority Penalty – a penalty that increases when high-priority packages are delivered later in the route.

•	Load Balance Penalty – a penalty based on how much unused capacity a vehicle has; encourages efficient use of vehicle load.
For each vehicle, the algorithm:

1.	Calculates the Euclidean distance traveled (including return to the shop).

2.	Applies a priority penalty, computed as the sum of (priority × delivery order index). This discourages delivering important packages last.

3.	Applies a load balance penalty, calculated as the absolute difference between the vehicle’s capacity and the total weight it carries. This penalizes underutilized or overloaded vehicles.

These penalties are scaled with coefficients (0.1 for priority, 0.05 for load balance) to keep them proportional and meaningful without overpowering the route cost.

The reason I chose this cost function design is to strike a balance between different goals—mainly minimizing travel distance, respecting package priority, and using vehicle capacity wisely. While reducing the total route distance is the main objective, I didn’t want the algorithm to ignore priority altogether. That’s why I added a priority penalty: it gently encourages the system to deliver high-priority packages earlier without forcing it. Sometimes, it may still delay a high-priority package if doing so greatly reduces the total travel distance, which is actually closer to a realistic and practical solution. The load penalty was added to avoid situations where one vehicle is overloaded and another is nearly empty. Instead, the algorithm tries to fill each vehicle closer to its full capacity, but only when it's smart to do so in terms of total cost.

The values I used (0.1 for the priority penalty and 0.05 for the load balance penalty) were chosen after experimenting and observing how the algorithm behaves. I wanted the route distance to remain the most important factor, so the penalties had to be smaller but still strong enough to influence the decision-making. If the priority penalty was too high, the algorithm would start making poor decisions just to deliver important packages first. If it was too low, it would completely ignore them. The same applies to the load balance: a gentle penalty (0.05) helps keep the load reasonable without making the algorithm obsess over perfect balance. This mix leads to more flexible, realistic, and high-quality solutions, especially in real-world delivery scenarios where perfect optimization isn’t always practical.

for more information and Details: check the Report.pdf file
