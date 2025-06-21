# ADR Process

## Overview

In the BigBone project, we use ADRs to log the outcome of design and decision making activities. Hence, we can view them
as journals, akin to meeting outcome summaries or discussion notes. ADRs help future developers to better understand why
things are done in a particular way.

## Process

The process for discussing architectural or design topics is initiated by first identifying a need for discussing it.
Once the team has agreed that there is a real need for having an in-depth discussion, the process is initiated as
follows:

1. The initiator of the topic creates a new discussion thread under the "Architecture & Design" category. The topic is
   discussed on the discussion thread and members agree on a decision to be taken.
2. The outcome of the discussion should be recorded in a new ADR inside the `/adr` folder. Please use the provided 
   template for this (`adr-template.md`). Give your ADR a meaningful name, such as `0001-exception-handling.md`. For
   every future ADR, the number should be incremented by 1.
3. Also please place a link in the discussion thread that points to the newly created ADR.
4. If concrete action needs to be taken, please open issues for all of them and refer to the ADR.

## More on ADRs

- https://adr.github.io/
- https://www.ozimmer.ch/practices/2023/04/03/ADRCreation.html (best practices, anti-patterns and such)