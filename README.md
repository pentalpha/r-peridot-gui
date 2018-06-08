![Project Logo](http://www.bioinformatics-brazil.org/r-peridot/img/logo1-no_background-black.png)

# R-Peridot-GUI
The power of the R's differential gene expression analysis packages, with no programming!

R-Peridot is a complete environment for differential gene expression analysis with RNASeq and Microarray data.

You just need to define your data, select what modules you want to use and specify some parameters.

R-Peridot will take care of the rest, efficiently parallelizing all the processing and saving the results for you!

[Project Page](www.bioinformatics-brazil.org/r-peridot)

## The Input
A plain text CSV/TSV table with genes/transcripts as rows and samples as columns, in which the values of the cells are read counts from RNA-Seq or Microarray experiments.

## The Idea
Abstracting the use of R packages by treating them as modules. Basically, the user only has to choose which modules he wants to use and R-Peridot takes care of the rest.

## What it can do
R-Peridot uses several Bioconductor packages for DGE analysis: DESeq, DESeq2, EBSeq, edgeR and sSeq. Each of these is use its own methodologies to find Differentially Expressed Genes (DEGs) among the ones in the input. 

The results of these is used to create differential expression consensus, with the intersection of all sets of DEGs. After that our software generates several graphs including Heat Maps, Dendrogram, PCA, Cluster Profiler and KEGG charts.

## Modules
The power of R-Peridot is only defined by its modules. Feel free to contribute with new modules that can create new results.

R-Peridot Modules Repository: [r-peridot-scripts](https://github.com/pentalpha/r-peridot-scripts)

## How to use

You can download our installers at the [Download Page](http://www.bioinformatics-brazil.org/r-peridot/download.html).

But you can also build it from the sources if you want:

### First, clone this repository:

```sh
    $ git clone https://github.com/pentalpha/r-peridot-gui.git
    $ cd r-peridot-gui/
```

### Now, install the dependencies:
R-Peridot's main dependencies are:

- OpenJDK 1.8;
- R >= 3.4.1;

If you are using GNU\Linux, there are more dependencies. There are scripts to handle the dependency installing at some distros:

- For Ubuntu >= 17.04: jar/ubuntuDeps.sh;
- For Debian >= 9: jar/debianDeps.sh;
- For CentOS >= 7.2: jar/centosDeps.sh (recommended for other rpm-based distros);

### It's time to build
This project uses [buildr](https://buildr.apache.org/) as build system. To build our project, please install it.

```sh
    $ buildr r-peridot-gui:make
```

That command will compile, package and them download the modules from [r-peridot-scripts](https://github.com/pentalpha/r-peridot-scripts). Always make sure that the 'r-peridot-scripts' directory is at the same directory as 'r-peridot-gui.jar'.

### Using it
R-Peridot can be executed by running the 'jar/r-peridot-gui' script or the 'jar/r-peridot-gui.jar' JAR file. You can move these to any place in your system, as long as you move them along with the 'r-peridot-scripts' directory.

When R-Peridot opens for the first time, it will ask you to choose a R environment. If that environment is missing packages, you have the option to install them.

#### We have written several guides about installing and using R-Peridot.

- [Installation Guide](http://www.bioinformatics-brazil.org/r-peridot/docs/installation_guide.pdf): Installing on GNU\Linux and Windows;
- [User's Guide](http://www.bioinformatics-brazil.org/r-peridot/docs/user_guide.pdf): The most complete reference on all the features of R-Peridot and how to use it;
- [Guide for Advanced Users](http://www.bioinformatics-brazil.org/r-peridot/docs/advanced_guide.pdf): How to modify and create modules for R-Peridot;

-------------------------------------------------------------
# The following software was used to make R-Peridot:

## R
Copyright (c) R Development Core Team, The R Foundation.
GNU GPL v2, full text: R-LICENSE.txt. 
Official webpage: <www.r-project.org>.

### OpenJDK 1.8
Available at: [http://openjdk.java.net/install/](http://openjdk.java.net/install/)

## NSIS
Used to generate the Windows installer.
Nullsoft Scriptable Install System, Copyright (C) 1999-2017 Contributors.
Available at: http://nsis.sourceforge.net/Main_Page. Accessed on 30-10-2017;

## R-Peridot-GUI uses, and does not modifies in any form, the binaries of the following Java Libraries:
### Apache Commons: Commons IO and Commons Lang.
Copyright (c)2017, The Apache Software Foundation.
Apache License Version 2.0, full text: APACHE-2-LICENSE.html
Available at: http://commons.apache.org/. Accessed on 17/08/2017;

### Substance Look and Feel:
Copyright (c)2017, Kirill Grouchnikov
All rights reserved. Full license: SUBSTANCE-LICENSE.txt.
Available at: https://github.com/kirill-grouchnikov/substance. Accessed on 17/08/2017;

--------------------------------------------------------------
# The following third-party graphic resources were used in R-Peridot-GUI:

## Developpers Icons by Sekkyumu
Available at: https://sekkyumu.deviantart.com/art/Developpers-Icons-63052312. Accessed on 30-10-2017;
## Must Have Icons by VisualPharm
Created by VisualPharm (https://visualpharm.com/);
CC BY-ND 3.0 license (https://creativecommons.org/licenses/by-nd/3.0/);
Available at: http://www.iconarchive.com/show/must-have-icons-by-visualpharm.html. Accessed on 30-10-2017;
