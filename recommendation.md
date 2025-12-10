┌────────────────────────────────────────────────────────────────────────┐
│     Curriculum Recommendation System - Three Recommendation Strategies  │
└────────────────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────────────────┐
│                          Input: Student Profile                          │
├─────────────────────────────────────────────────────────────────────────┤
│  • Student ID: 1                                                        │
│  • GPA: 3.667                                                          │
│  • Major: Information Systems                                          │
│  • Career Interests: "Software Engineering, Java, Algorithms"         │
│  • Completed Courses: [INFO5100, INFO6150]                            │
└─────────────────────────────────────────────────────────────────────────┘
                                    ↓
                    ┌───────────────┼───────────────┐
                    │               │               │
                    ▼               ▼               ▼

┌──────────────────┐  ┌──────────────────┐  ┌──────────────────┐
│  Strategy 1:     │  │  Strategy 2:     │  │  Strategy 3:     │
│  Comprehensive   │  │  Degree          │  │  Popular         │
│  (Next Semester) │  │  Requirements    │  │  Courses         │
└──────────────────┘  └──────────────────┘  └──────────────────┘

┌─────────────────────────────────────────────────────────────────────────┐
│ Strategy 1: Comprehensive Recommendation (Next Semester)                 │
│ API: GET /recommendations/{studentId}                                   │
├─────────────────────────────────────────────────────────────────────────┤
│                                                                         │
│  Scoring Factors (Total: 110 points):                                  │
│                                                                         │
│  ┌─────────────────────────────────────────────────────────┐          │
│  │ Factor 1: GPA Matching (30 points)                      │          │
│  │ ─────────────────────────────────────────────────────── │          │
│  │ if GPA ≥ 3.5 && difficulty = ADVANCED    → +30 pts     │          │
│  │ if 3.0 ≤ GPA < 3.5 && difficulty = INTER → +30 pts     │          │
│  │ if GPA < 3.0 && difficulty = BEGINNER    → +30 pts     │          │
│  │                                                          │          │
│  │ Student GPA 3.667 → Suitable for ADVANCED courses ✓     │          │
│  └─────────────────────────────────────────────────────────┘          │
│                                                                         │
│  ┌─────────────────────────────────────────────────────────┐          │
│  │ Factor 2: Prerequisite Verification (40 points) - BFS    │          │
│  │ ─────────────────────────────────────────────────────── │          │
│  │ 1. Use BFS to get all prerequisite levels               │          │
│  │    Example: INFO7245 requires {INFO6205: L1, INFO5100: L2}│        │
│  │                                                          │          │
│  │ 2. Check if student completed all prerequisites          │          │
│  │    Completed: [INFO5100 ✓, INFO6150 ✓]                 │          │
│  │                                                          │          │
│  │ 3. Scoring:                                              │          │
│  │    if all prerequisites met → +40 pts                    │          │
│  │    if missing prerequisites → skip course (don't recommend)│        │
│  │                                                          │          │
│  │ Return prerequisiteChain for frontend visualization      │          │
│  └─────────────────────────────────────────────────────────┘          │
│                                                                         │
│  ┌─────────────────────────────────────────────────────────┐          │
│  │ Factor 3: Career Interest Matching (30 points)           │          │
│  │ ─────────────────────────────────────────────────────── │          │
│  │ 1. Student interest keywords:                            │          │
│  │    ["Software Engineering", "Java", "Algorithms"]       │          │
│  │                                                          │          │
│  │ 2. Course career tags (priority match):                  │          │
│  │    Course.careerTags =                                  │          │
│  │    "Software Engineering,Java Programming,Algorithms"   │          │
│  │                                                          │          │
│  │ 3. Count matches: matchCount = 3 (full match)            │          │
│  │                                                          │          │
│  │ 4. Scoring:                                              │          │
│  │    if matchCount ≥ 3 → +30 pts (strong match)           │          │
│  │    if matchCount = 2 → +20 pts (moderate)               │          │
│  │    if matchCount = 1 → +10 pts (weak match)             │          │
│  └─────────────────────────────────────────────────────────┘          │
│                                                                         │
│  ┌─────────────────────────────────────────────────────────┐          │
│  │ Factor 4: Semester Matching (Bonus 10 points)            │          │
│  │ ─────────────────────────────────────────────────────── │          │
│  │ Determine next semester:                                 │          │
│  │   if current month 1-5  → next = Spring                 │          │
│  │   if current month 6-8  → next = Fall                   │          │
│  │   if current month 9-12 → next = Spring                 │          │
│  │                                                          │          │
│  │ if course offered in next semester → +10 pts             │          │
│  │ if course offered both Fall/Spring → +5 pts              │          │
│  └─────────────────────────────────────────────────────────┘          │
│                                                                         │
│  Sample Output:                                                         │
│  ┌────────────────────────────────────────────────────────┐           │
│  │ Course: INFO 6205 - Data Structures                    │           │
│  │ Match Score: 100.0                                     │           │
│  │ Reason: "Your high GPA suits advanced courses. All    │           │
│  │         prerequisites met. Strongly aligns with your   │           │
│  │         career interests. Available in Fall semester." │           │
│  │                                                        │           │
│  │ Prerequisite Chain:                                    │           │
│  │   [✓] INFO 5100 (Level 1) - Completed                │           │
│  └────────────────────────────────────────────────────────┘           │
│                                                                         │
│  Use Case: Semester course planning, personalized recommendations       │
└─────────────────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────────────────┐
│ Strategy 2: Degree Requirements Recommendation                          │
│ API: GET /recommendations/{studentId}/degree-requirements               │
├─────────────────────────────────────────────────────────────────────────┤
│                                                                         │
│  Data Source: ProgramRequirement Table                                  │
│  ┌────────────────────────────────────────────┐                        │
│  │ major = "Information Systems"              │                        │
│  │ ├─ INFO 5100: Core, Mandatory              │                        │
│  │ ├─ INFO 6150: Core, Mandatory              │                        │
│  │ ├─ INFO 6205: Core, Mandatory              │                        │
│  │ ├─ INFO 6250: Elective, Optional           │                        │
│  │ └─ ...                                     │                        │
│  └────────────────────────────────────────────┘                        │
│                                                                         │
│  Scoring Factors (Total: 100 points):                                  │
│                                                                         │
│  ┌─────────────────────────────────────────────────────────┐          │
│  │ Base Score: 50 points                                    │          │
│  │                                                          │          │
│  │ + If Mandatory (isMandatory=true)     → +30 pts         │          │
│  │ + If Elective (isMandatory=false)     → +10 pts         │          │
│  │                                                          │          │
│  │ + Course Type Bonus:                                     │          │
│  │   - Core course        → +10 pts                        │          │
│  │   - Foundation course  → +5 pts                         │          │
│  │   - Elective course    → +0 pts                         │          │
│  │                                                          │          │
│  │ + Prerequisites met    → +10 pts                         │          │
│  │   Prerequisites missing → Still show, but mark as needed │          │
│  └─────────────────────────────────────────────────────────┘          │
│                                                                         │
│  Sorting Logic:                                                         │
│  Required Core > Required Foundation > Required Elective > Electives    │
│                                                                         │
│  Sample Output:                                                         │
│  ┌────────────────────────────────────────────────────────┐           │
│  │ 1. INFO 6205 - Data Structures                         │           │
│  │    Score: 90                                           │           │
│  │    Reason: "Required core course for Information       │           │
│  │            Systems major. Core course. Prerequisites   │           │
│  │            met."                                       │           │
│  │    Type: Core | Mandatory: Yes                        │           │
│  │                                                        │           │
│  │ 2. INFO 7245 - Advanced Algorithms                     │           │
│  │    Score: 80                                           │           │
│  │    Reason: "Required core course. Prerequisites needed:│           │
│  │            INFO 6205"                                  │           │
│  │    Type: Core | Mandatory: Yes                        │           │
│  │    Missing: [INFO 6205]                                │           │
│  └────────────────────────────────────────────────────────┘           │
│                                                                         │
│  Features:                                                              │
│  • Focus on graduation requirements                                     │
│  • Prioritize mandatory courses                                         │
│  • Show missing prerequisites (help students plan)                      │
│                                                                         │
│  Use Case: Degree completion planning, view required courses            │
└─────────────────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────────────────┐
│ Strategy 3: Popular Courses Recommendation                              │
│ API: GET /recommendations/{studentId}/popular                           │
├─────────────────────────────────────────────────────────────────────────┤
│                                                                         │
│  Data Source: Real-time Statistics from Enrollments Table               │
│  ┌────────────────────────────────────────────┐                        │
│  │ SQL Query:                                 │                        │
│  │ SELECT course_id,                          │                        │
│  │        COUNT(*) as enrollment_count,       │                        │
│  │        AVG(grade_to_gpa) as avg_grade      │                        │
│  │ FROM enrollments                           │                        │
│  │ WHERE status = 'COMPLETED'                 │                        │
│  │ GROUP BY course_id                         │                        │
│  └────────────────────────────────────────────┘                        │
│                                                                         │
│  Scoring Factors (Total: 100 points):                                  │
│                                                                         │
│  ┌─────────────────────────────────────────────────────────┐          │
│  │ Factor 1: Popularity Score (50 points)                  │          │
│  │ ─────────────────────────────────────────────────────── │          │
│  │ Formula: popularityScore = (enrollmentCount × 0.7) +    │          │
│  │                            (avgGrade × 10 × 0.3)        │          │
│  │                                                          │          │
│  │ Example:                                                 │          │
│  │   INFO 6205: 45 students, avg GPA 3.8                   │          │
│  │   = (45 × 0.7) + (3.8 × 10 × 0.3)                       │          │
│  │   = 31.5 + 11.4 = 42.9                                  │          │
│  │                                                          │          │
│  │ Normalized to 50 points:                                 │          │
│  │   (42.9 / maxPopularity) × 50                           │          │
│  └─────────────────────────────────────────────────────────┘          │
│                                                                         │
│  ┌─────────────────────────────────────────────────────────┐          │
│  │ Factor 2: Average Grade Bonus (30 points)               │          │
│  │ ─────────────────────────────────────────────────────── │          │
│  │ if avgGrade ≥ 3.5 → +30 pts (high grades, easier to ace)│          │
│  │ if avgGrade ≥ 3.0 → +20 pts (moderate)                  │          │
│  │ if avgGrade ≥ 2.5 → +10 pts (passing)                   │          │
│  │                                                          │          │
│  │ Student perspective: "High average means I can succeed" │          │
│  └─────────────────────────────────────────────────────────┘          │
│                                                                         │
│  ┌─────────────────────────────────────────────────────────┐          │
│  │ Factor 3: Prerequisites Met (20 points)                  │          │
│  │ ─────────────────────────────────────────────────────── │          │
│  │ if all prerequisites satisfied → +20 pts                 │          │
│  │ if missing prerequisites       → skip course             │          │
│  └─────────────────────────────────────────────────────────┘          │
│                                                                         │
│  Sample Output:                                                         │
│  ┌────────────────────────────────────────────────────────┐           │
│  │ 1. INFO 6205 - Data Structures                         │           │
│  │    Score: 98.5                                         │           │
│  │    Reason: "Popular course with 45 students enrolled.  │           │
│  │            High average grade (3.82). Prerequisites    │           │
│  │            met."                                       │           │
│  │    Students Enrolled: 45                               │           │
│  │    Average Grade: A- (3.82)                           │           │
│  └────────────────────────────────────────────────────────┘           │
│                                                                         │
│  Features:                                                              │
│  • Discover high-quality courses (high grades + popular)                │
│  • Real-time enrollment statistics                                      │
│  • Ideal for students exploring options                                 │
│                                                                         │
│  Use Case: Find popular courses, discover high-grade courses            │
└─────────────────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────────────────┐
│                    Strategy Comparison Matrix                            │
├──────────────┬──────────────────┬──────────────────┬───────────────────┤
│ Strategy     │ Core Focus       │ Weight Allocation│ Use Case          │
├──────────────┼──────────────────┼──────────────────┼───────────────────┤
│ Comprehensive│ GPA+Prereq       │ 30+40+30+10      │ Personalized      │
│ Next Semester│ +Interest        │ (110 points)     │ semester planning │
│              │ +Semester        │                  │                   │
├──────────────┼──────────────────┼──────────────────┼───────────────────┤
│ Degree       │ Mandatory>Type   │ 50+30+15+5       │ Graduation        │
│ Requirements │ +Prerequisites   │ (100 points)     │ requirement track │
├──────────────┼──────────────────┼──────────────────┼───────────────────┤
│ Popular      │ Enrollment       │ 50+30+20         │ Discover quality  │
│ Courses      │ +Avg Grade       │ (100 points)     │ courses           │
│              │ +Prerequisites   │                  │                   │
└──────────────┴──────────────────┴──────────────────┴───────────────────┘

┌─────────────────────────────────────────────────────────────────────────┐
│              Core Algorithm: BFS Prerequisite Chain Checking             │
│                      (Shared by all strategies)                          │
├─────────────────────────────────────────────────────────────────────────┤
│                                                                         │
│  Problem: How to check chained prerequisites?                           │
│                                                                         │
│  Example: What does INFO 7245 require?                                  │
│  ┌────────────────────────────────────────────┐                        │
│  │                                            │                        │
│  │  INFO 5100 (Application Engineering)      │                        │
│  │       ↓ prerequisite                       │                        │
│  │  INFO 6205 (Data Structures)               │                        │
│  │       ↓ prerequisite                       │                        │
│  │  INFO 7245 (Advanced Algorithms)           │                        │
│  │                                            │                        │
│  └────────────────────────────────────────────┘                        │
│                                                                         │
│  BFS Algorithm Execution:                                               │
│  ┌────────────────────────────────────────────────────────┐           │
│  │ Queue = [INFO7245]                                     │           │
│  │ Visited = {}                                           │           │
│  │ Result = {}                                            │           │
│  │ Level = 1                                              │           │
│  │                                                        │           │
│  │ Round 1 (Level 1 - Direct Prerequisites):             │           │
│  │   Process: INFO7245                                    │           │
│  │   Find prereq: INFO6205                                │           │
│  │   Result = {INFO6205: Level 1}                         │           │
│  │   Queue = [INFO6205]                                   │           │
│  │                                                        │           │
│  │ Round 2 (Level 2 - Indirect Prerequisites):           │           │
│  │   Process: INFO6205                                    │           │
│  │   Find prereq: INFO5100                                │           │
│  │   Result = {INFO6205: L1, INFO5100: L2}                │           │
│  │   Queue = [INFO5100]                                   │           │
│  │                                                        │           │
│  │ Round 3 (Level 3):                                     │           │
│  │   Process: INFO5100                                    │           │
│  │   No prereqs → Stop                                    │           │
│  │                                                        │           │
│  │ Final Result: {INFO6205: 1, INFO5100: 2}               │           │
│  └────────────────────────────────────────────────────────┘           │
│                                                                         │
│  Return to Frontend:                                                    │
│  ┌────────────────────────────────────────────────────────┐           │
│  │ prerequisiteChain: [                                   │           │
│  │   {courseCode: "INFO5100", level: 2, completed: ✓},   │           │
│  │   {courseCode: "INFO6205", level: 1, completed: ✓}    │           │
│  │ ]                                                      │           │
│  │ missingPrerequisites: []                               │           │
│  │ allPrerequisitesMet: true                              │           │
│  └────────────────────────────────────────────────────────┘           │
│                                                                         │
│  Algorithm Advantages:                                                  │
│  ✓ Avoids recursive stack overflow                                     │
│  ✓ Tracks depth levels automatically                                   │
│  ✓ Time Complexity: O(V+E)                                             │
│  ✓ Prevents circular dependencies                                      │
└─────────────────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────────────────┐
│                    Recommendation Decision Tree                          │
├─────────────────────────────────────────────────────────────────────────┤
│                                                                         │
│                    Student Needs Course                                 │
│                            ↓                                            │
│                    ┌───────┴───────┐                                   │
│                    │  What Need?   │                                   │
│                    └───────┬───────┘                                   │
│                            │                                            │
│          ┌─────────────────┼─────────────────┐                        │
│          ↓                 ↓                 ↓                          │
│   ┌──────────────┐  ┌──────────────┐  ┌──────────────┐              │
│   │Personalized? │  │Graduate?     │  │Find quality? │              │
│   │Next semester?│  │Need required?│  │Easy A?       │              │
│   └──────┬───────┘  └──────┬───────┘  └──────┬───────┘              │
│          │                 │                 │                          │
│          ↓                 ↓                 ↓                          │
│   ┌──────────────┐  ┌──────────────┐  ┌──────────────┐              │
│   │Comprehensive │  │   Degree     │  │   Popular    │              │
│   │(Next Sem)    │  │ Requirements │  │   Courses    │              │
│   └──────────────┘  └──────────────┘  └──────────────┘              │
│          │                 │                 │                          │
│          ↓                 ↓                 ↓                          │
│   GPA+Interest    Only major courses   Enrollment stats                │
│   +Next semester  Required first       +High avg grade                 │
│                                                                         │
└─────────────────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────────────────┐
│                    Algorithm Complexity Analysis                         │
├─────────────────────────────────────────────────────────────────────────┤
│                                                                         │
│ BFS Prerequisite Check:                                                 │
│   Time Complexity:  O(V + E)   V=courses, E=prerequisite relationships  │
│   Space Complexity: O(V)       Store visited set and queue             │
│                                                                         │
│ GPA Calculation:                                                        │
│   Time Complexity:  O(N)       N=completed courses                     │
│   Space Complexity: O(1)                                               │
│                                                                         │
│ Career Interest Matching:                                               │
│   Time Complexity:  O(K × M)   K=interest keywords, M=course tags      │
│   Space Complexity: O(1)                                               │
│                                                                         │
│ Popularity Statistics:                                                  │
│   Time Complexity:  O(E)       E=all enrollment records                │
│   Space Complexity: O(C)       C=courses (store stats Map)             │
│                                                                         │
│ Overall Recommendation Generation:                                      │
│   Time Complexity:  O(C × (V + E + N + K×M))                           │
│   Actual Performance: 200 courses, 50 prerequisites, <500ms response   │
│                                                                         │
└─────────────────────────────────────────────────────────────────────────┘
