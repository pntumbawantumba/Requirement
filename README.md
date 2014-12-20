Requirement
===========

Ce projet est un logiciel d’élaboration des exigences (Requirement). 
On se conformera à la définition des exigences telles qu’elles sont définies dans le langage de modélisation SysML

L’application comprend deux parties :
1.
Un projet OSGI "requirementServer" fournissant un service HTTP via Jetty et permettant d’écrire des servlets.

2. Une application Android "requirementAPP" qui peut invoquer un servlet pour stocker le jeu de données en cours 
de création ou de modification, pour connaître les jeux de données disponibles et pour récupérer un jeu de données parmi ceux disponibles
