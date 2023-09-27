# Teaching AI How To Play Snake (Using NEAT)

This project serves as my introduction to the algorithm [NeuroEvolution of Augmenting Topologies (NEAT)](https://nn.cs.utexas.edu/downloads/papers/stanley.ec02.pdf). 

Here I will attempt to explain how it works. Videos to come out after I polish everything up.

## Introduction

[NeuroEvolution of Augmenting Topologies (NEAT)](https://nn.cs.utexas.edu/downloads/papers/stanley.ec02.pdf) is a genetic algorithm for evolving artificial neural networks. It applies three key techniques: tracking genes with history markers to allow neural networks with differing topologies to crossbreed, differentiating individual neural networks into species to preserve innovations, and incrementally developing topologies from a barebones network (just input/output nodes) to keep networks as simple as possible.

In layman's terms, the algorithm is analagous to Darwinian evolution. We start off with a bunch of individual beings (neural networks), which each have their own set of genes. Beings with similar genes to each other are grouped into species. Just like in nature, survival of the fittest determines who lives and gets to breed, and who dies. To preserve genetic diversity, beings only have to compete within their species. We want genetic diversity in case a really good long-term strategy starts off poorly; to ensure it doesn't die prematurely, we provide a safe bubble for it to develop and mature.

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
  * It has a genome, score, and belongs in a species.
* A **Species** is a collection of Individuals which share similar genomes.
  * It has a list of all the Individuals belonging to this species, as well as their average scores.
  * One of the Individuals is a "representative", and it is the first Individual in the species, to which all other Indivudals are compared to in order to gain entry to the species.
 

## Algorithm

(Work in Progress)
