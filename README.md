# Cleavage code analyzer

## What is it?

Cleavage looks at a repository (currently only git), and then generates a scatter plot
of files against complexity (currently only measured by cyclomatic complexity, and only for java files)
 and number of commits.

It then superimposes this same scatter plot back in time relative to each historical revision in
the past to create a third axis of time. By connecting a single file's points on these scatter plots
we can create tendrils showing complexity over time. When a tendril breaks out of the pack, it has cleaved
from the average complexity/commit measure of your codebase. Maybe you want to look at that file?

## How do I use it?

Cleavage operates against git repositories. Right now I invoke it from a REPL, started up from cake.

First you'll need cake

    gem install cake

Next, from the root of the Cleavage project, start up a REPL

    cake repl

Get all the Cleavage goodies in your REPL

    (require 'cleavage.core)

Invoke it against some git repository. (The trailing slash is important right now, this is a lame bug.)

    (cleavage.core/cleavage "/Users/alex/mongo-java-driver/")

I pick on mongo-java-driver because it was the first java project I found on github when I did a search.


## What does it do?

It looks at the 15 most recent commits in your git repo, and generates a scatter plot for each commit of
complexity vs. # of commits for each file. It creates layered scatter plots (putting pie plates down for
each point) based on this information. It does not provide any means of identifying what file is 
represented by each of these stacks of points.

## Why doesn't it do X?

Because I've spent three days playing with it by myself. If you'd like to contribute, please fork it, 
make your changes in a topic branch, and then make a pull request. Tests are nice, but I've been slacking
on adding them so far, so you can too.

## I want to know which file is going out of control!

My understanding right now is that Penumbra doesn't support the GL_SELECT rendering mode, which I think would
be the best tool for letting people identify a specific tendril. After I like the state of rendering in Cleavage
I intend to spend some time seeing about contributing changes to Penumbra.
