# Teaching AI How To Play Snake (Using NEAT)

This project serves as my introduction to the algorithm [NeuroEvolution of Augmenting Topologies (NEAT)](https://nn.cs.utexas.edu/downloads/papers/stanley.ec02.pdf). 

Here I will attempt to explain how it works. Videos to come out after I polish everything up.

## Introduction

[NeuroEvolution of Augmenting Topologies (NEAT)](https://nn.cs.utexas.edu/downloads/papers/stanley.ec02.pdf) is a genetic algorithm for evolving artificial neural networks. It applies three key techniques: tracking genes with history markers to allow neural networks with differing topologies to crossbreed, differentiating individual neural networks into species to preserve innovations, and incrementally developing topologies from a barebones network (just input/output nodes) to keep networks as simple as possible.

In layman's terms, the algorithm is analagous to Darwinian evolution. We start off with a bunch of individual beings (neural networks), which each have their own set of genes. Beings with similar genes to each other are grouped into species. Just like in nature, survival of the fittest determines who lives and gets to breed, and who dies. To preserve genetic diversity, beings only have to compete within their species. We want genetic diversity in case a really good long-term strategy starts off poorly; to ensure it doesn't die prematurely, we provide a safe bubble for it to develop and mature.

(Work in Progress)
