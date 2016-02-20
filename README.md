# procgenf
Procedural generation system

[![Build Status](https://travis-ci.org/fachammer/procgenf.svg?branch=master)](https://travis-ci.org/fachammer/procgenf)

## Running the infinite Voronoi treemap example

1. Clone the repository recursively (because a submodule is included):

         git clone --recursive https://github.com/fachammer/procgenf.git

1. Change to the project directory:

         cd procgenf

1. Run the Gradle Wrapper to download Gradle and all project dependencies, build the project and start the infinite Voronoi treemap:

        ./gradlew

### Controls

WASD: Move around
Arrow key up/down: Zoom in/out
F: toggle between adaptive and fixed-size visibility rectangle
1: Set *noise* generation method
2: Set *hexagon* generation method
R: Set random seed (only has effect in *noise* generation mode)