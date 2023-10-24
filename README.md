# Teaching AI How To Play Snake (Using NEAT)

This project serves as my introduction to the algorithm [NeuroEvolution of Augmenting Topologies (NEAT)](https://nn.cs.utexas.edu/downloads/papers/stanley.ec02.pdf). 

Here I will attempt to explain how it works. Videos to come out after I polish everything up.

## Introduction

[NeuroEvolution of Augmenting Topologies (NEAT)](https://nn.cs.utexas.edu/downloads/papers/stanley.ec02.pdf) is a genetic algorithm for evolving artificial neural networks. It applies three key techniques: tracking genes with history markers to allow neural networks with differing topologies to crossbreed, differentiating individual neural networks into species to preserve innovations, and incrementally developing topologies from a barebones network (just input/output nodes) to keep networks as simple as possible.

In layman's terms, the algorithm is analogous to Darwinian evolution. We start off with a bunch of individual beings (neural networks), which each have their own set of genes. Beings with similar genes to each other are grouped into species. Just like in nature, survival of the fittest determines who lives and gets to breed, and who dies. To preserve genetic diversity, beings only have to compete within their species. We want genetic diversity in case a really good long-term strategy starts off poorly; to ensure it doesn't die prematurely, we provide a safe bubble for it to develop and mature.

## Structure
### Genes and Genomes
* A **Gene** is a single component of the neural network.
* A **Node Gene** represents a node in the neural network.
  * Each Node Gene has an x (used to prevent loops when forming connections) and a y value (used for the GUI), as well as an innovation number (explained later).
* A **Connection Gene** represents a connection between two nodes in the neural network.
  * Each Connection Gene specifies the two nodes it is connected to, the weight of the connection, whether it is enabled or disabled, and its innovation number (explained later).
* A **Genome** is the collection of all the genes that make up the neural network.

![image](https://github.com/CanadianCrafter/NEAT/assets/62266519/bbfc72fb-aee4-4f0a-96e4-9aff8d755c8a)

### Individuals and Species
* An **Individual** is an individual neural network
  * It has a genome, score, and belongs to a Species.
* A **Species** is a collection of Individuals which share similar genomes.
  * It has a list of all the Individuals belonging to this species, as well as their average scores.
  * One of the Individuals is a "representative", and it is the first Individual in the species, to which all other Individuals are compared to in order to gain entry to the species.
 

## Algorithm
### Start
* We start with a collection of Individuals.
* Each neural network (Individual) starts off with only input and output nodes. It has no connections and no hidden nodes (nodes in between the input and the output).
### Evolution
* Perform speciation by grouping the individuals into species.
* Kill the worst performing n% individuals of each species (where n is a predefined constant called the death rate).
* After culling each species, if a species has one or fewer individuals, it goes extinct and we remove them.
* For every individual we have killed, we replace it with the offspring of two randomly chosen individuals from the same species (the species is also randomly chosen).
* Every Individual now undergoes mutation.
* We repeat this step for however many generations we specified for training.

### Speciation


### Crossbreeding
* An **Innovation Number** is an identification number that determines when a connection has been created.
  * They are assigned to newly created connections in any Individuals (innovation numbers are shared between all individuals).
  * If a connection between two NodeGenes already exists in a different network, it will simply inherit that ConnectionGene's innovation number.
  * Otherwise, it gets an innovation number one greater than the number of all ConnectionGenes (as to ensure uniqueness between innovation numbers).
* To breed two Individuals, we take the ConnectionGenes of the two Individuals, and iterate through the Genes in each using a separate index for each.
  * We let the fitter Genome be Genome 1, and the less fit Genome, Genome 2.
  * We rely on innovation numbers to locate shared Genes between the two Genomes.
    * This works since genes in a genome are ordered by innovation numbers in increasing order.
* If two Genes have the same innovation number, there is a 50% chance we copy the Gene from Genome 1 and a 50% chance we copy from Genome 2.
* If the innovation number of Genome 1 is larger than that of Genome 2, it means that Genome 2 has a disjoint gene here (meaning it's a Gene that Genome 1 doesn't have).
  * We don't add this disjoint Gene since it is from the less-fit Genome.
* If the innovation number of Genome 1 is smaller than that of Genome 2, it means that Genome 1 has a disjoint gene here. We add it to the child since it is from the fitter gene.

### Mutation
* An Individual can be mutated in the following ways:
  * A new connection is created between two randomly selected nodes.
    * Two NodeGenes from the Individual are selected randomly.
    * When we create a new connection, the first node's x position must be strictly smaller than the second node's.
      * This is to prevent loops in the neural network.
    * If the two NodeGenes we selected happen to have the same x position, we simply have to pick again.
    * If both have the same x position, we also have to pick again.
    * Since this is based on chance, we attempt to add a connection a predetermined amount of times before we give up.
  * A new node is created between a connection of two existing nodes. The old connection is also split in two, so a new connection must be made.
    * This new connection also needs its own innovation number (equal to one plus the number of existing connections, as to ensure the innovation number's uniqueness).  
  * A randomly selected connection has its weight shifted by a random amount. This random amount is bounded by a predetermined weight shift strength.
  * A randomly selected connection has its weight replaced by a random amount. This random amount is bounded by a predetermined weight randomized strength.
  * A randomly selected connection is toggled off if it is on, and toggled on if it is off. 
* Each of the mutations has a probability of occurring, and is based on preset constants.
* Furthermore, each probability is calculated separately so an individual may experience multiple mutations in one call to the mutation function (or in other words, each generation).

(Work in Progress)
