## Random mutation & fitness-based selection simulator

Java command line app to simulate random mutation and non-random fitness-based selection. This is based on the string mutation that Richard Dawkin discusses in The Blind Watchmaker (chapter 3). This application introduces further complexity to the algorithm by allowing the following controlability:
1. Evolution space: Control the evolutionary space gap between initial parent and last child (target)
2. Mutation stability: Stabilize mutation when the group reaches a stable size by controlling the mutation probability.
3. Range-constrained mutation: Limit mutation to occur within configurable constrained range for the mutable unit.
4. Generational fitness: Control generational fitness by varying the number of children per generation.

**Usage:**
1. Compile: javac MeThinksItsLikeAWeasel.java
2. Configure app in app.config. Following configuration parameters are available:
   - config.firstParent=random string
   - config.target=METHINKS IT IS LIKE A WEASEL
   - config.stableStrandSize=28
   - config.stableStrandMutationProbability=0.05
   - config.doRangeControlledMutation=true
   - config.mutationRange=10
   - config.numberOfChildrenPerGeneration=75
   - config.printEveryNthGeneration=1
3. Save app.config
4. Run: java MeThinksItsLikeAWeasel

**Configuration parameters:**
1. **config.firstParent:** This is the initial string that the application uses to generate child strings.
   <br/>Default: Random string of size of config.target
2. **config.target:** This is the target string
3. **config.stableStrandSize:** When the mutable unit forms a group of this size, the probability of mutation goes down to a configured probability value via config.stableStrandMutationProbability [0,1]
4. **config.stableStrandMutationProbability:** See config.stableStrandSize
5. **config.doRangeControlledMutation:** Enable this to limit mutation of each unit within it's local range. The range size can be configured via config.mutationRange. The new value of unit that undergoes the mutation will lie within [-config.mutationRange, +config.mutationRange]
   <br/>Default: false
6. **config.mutationRange:** See config.doRangeControlledMutation
7. **config.numberOfChildrenPerGeneration:** This configures number of offsprings that will be created for each parent. The next parent will be the fittest (lowest deviation index) child from this group. 
8. **config.printEveryNthGeneration:** This only prints the generational information every nth generation
