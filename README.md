# Cleavage code analyzer

Cleavage looks at a repository (currently only git), and then generates a scatter plot
of files against complexity (currently only measured by cyclomatic complexity, and only for java files)
 and number of commits.

It then superimposes this same scatter plot back in time relative to each historical revision in
the past to create a third axis of time. By connecting a single file's points on these scatter plots
we can create tendrils showing complexity over time. When a tendril breaks out of the pack, it has cleaved
from the average complexity/commit measure of your codebase. Maybe you want to look at that file?

## How do I use it?

Well it's a work in progress right now, hard coded to look at a clone of the mongo java driver on my 
local hard disk. If I actually turn this into a useful tool I will make sure you are the first to know.
