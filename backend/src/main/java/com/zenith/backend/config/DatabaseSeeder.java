package com.zenith.backend.config;

import com.zenith.backend.model.PlacementNote;
import com.zenith.backend.repository.PlacementNoteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import java.util.List;

@Component
public class DatabaseSeeder implements CommandLineRunner {

    private final PlacementNoteRepository noteRepository;

    @Autowired
    public DatabaseSeeder(PlacementNoteRepository noteRepository) {
        this.noteRepository = noteRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        if (noteRepository.count() == 0) {
            System.out.println("Seeding database with default placement notes for RAG...");

            List<PlacementNote> notes = List.of(
                PlacementNote.builder()
                        .title("Google Software Engineer Placement Guide & Experience")
                        .content("Google's interview process typically consists of: " +
                                "1. Online Assessment (OA) containing 2 coding questions (Medium-Hard difficulty) focusing on Arrays, Graphs, or Trees. " +
                                "2. 3-4 Technical Rounds focusing on Data Structures & Algorithms, Space-Time Complexity optimizations, and System Design (for L4/Senior). " +
                                "3. Googliness & Leadership Round to test behavioral fit. " +
                                "Key topics to prepare: Graph algorithms (DFS, BFS, Dijkstra, MST), Dynamic Programming, Recursion/Backtracking, and System Design concepts like Load Balancing, Partitioning, and Caching. " +
                                "Average compensation ranges from 18 to 45 LPA.")
                        .category("COMPANY")
                        .tags("google, placement, interview, software engineer, salary, hiring, oa")
                        .build(),

                PlacementNote.builder()
                        .title("DBMS Transactions: ACID Properties and Concurrency Control")
                        .content("A database transaction is a unit of program execution that accesses or updates data. It must satisfy ACID properties: " +
                                "1. Atomicity: All operations occur, or none do. " +
                                "2. Consistency: Database remains consistent before and after transactions. " +
                                "3. Isolation: Transactions are isolated from concurrent operations to avoid anomalies like Dirty Reads or Phantom Reads. " +
                                "4. Durability: Updates persist in database memory. " +
                                "Concurrency control protocols include Two-Phase Locking (2PL), Strict 2PL, and Timestamp-based protocols which maintain Serializability.")
                        .category("CS")
                        .tags("dbms, database, acid, transactions, isolation, concurrency, sql")
                        .build(),

                PlacementNote.builder()
                        .title("Operating Systems: CPU Scheduling & Deadlocks")
                        .content("CPU Scheduling algorithms determine which ready process gets allocated the CPU. Key algorithms include First-Come-First-Serve (FCFS), Shortest Job First (SJF), Shortest Remaining Time First (SRTF), Round Robin (RR), and Priority Scheduling. " +
                                "A Deadlock is a state where processes are blocked waiting for resources held by each other. Four necessary conditions for deadlock are: " +
                                "1. Mutual Exclusion, 2. Hold and Wait, 3. No Preemption, and 4. Circular Wait. Deadlocks are avoided using the Banker's Algorithm and resolved by aborting processes or preemption.")
                        .category("CS")
                        .tags("os, operating system, scheduling, deadlock, bankers algorithm, process")
                        .build(),

                PlacementNote.builder()
                        .title("DSA Sheet: Top Graph & Tree Algorithms for OA")
                        .content("Graph traversals DFS and BFS are fundamental. DFS uses a recursion stack; BFS uses a FIFO queue. " +
                                "Shortest Path algorithms: Dijkstra's (greedy, handles non-negative edges, O(V log V + E log V) with min-heap), Bellman-Ford (handles negative weights, detects negative cycles, complexity O(VE)). " +
                                "Minimum Spanning Tree (MST): Prim's and Kruskal's (uses Disjoint Set Union). " +
                                "Tree Traversals: Inorder (Left-Root-Right), Preorder (Root-Left-Right), Postorder (Left-Right-Root), and Level-Order (BFS).")
                        .category("DSA")
                        .tags("dsa, graph, tree, dijkstra, bfs, dfs, traversal, mst")
                        .build(),

                PlacementNote.builder()
                        .title("Amazon Leadership Principles & Behavioral Preparation")
                        .content("Amazon interviews weigh their 16 Leadership Principles (LPs) heavily. Every technical round has 10-15 minutes of LP questions. " +
                                "Key principles: Customer Obsession, Ownership, Bias for Action, Deliver Results, and Dive Deep. " +
                                "Answer behavioral questions using the STAR method: " +
                                "- Situation: Describe the context. " +
                                "- Task: Detail the challenge/goal. " +
                                "- Action: What you did personally (focus on 'I' instead of 'We'). " +
                                "- Result: The quantifiable outcome (e.g., improved latency by 15%).")
                        .category("COMPANY")
                        .tags("amazon, placement, interview, leadership principles, behavioral, star method")
                        .build()
            );

            noteRepository.saveAll(notes);
            System.out.println("Successfully seeded database with " + notes.size() + " placement notes!");
        }
    }
}
