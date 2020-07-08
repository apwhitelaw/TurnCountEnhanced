# TurnCountEnhanced

At the company I work for, we collect vehicle traffic data. We use a proprietary software that has not been updated in a long time. There are some known bugs, and we have always had ideas for new features that could help increase workflow. I thought it would be interesting to try to recreate it as well as try to improve upon it. 
Currently the software doesn't output anything, but the functionality of counting and changing intervals all works.

Please note there is no intent for copyright infringement on the software itself or the name. This is not for commercial use, it is simply a proof of ability.

### Interface

The main interface is a TabPane that displays the counted movements for the current interval. The tabs allow you to view the other banks. There is also a menu bar as well as buttons for changing intervals, clearing intervals, etc.

### Intervals

Data collection is done through the use of intervals. An interval is a 5 minute period of time, and the cars are captured for those 5 minutes. Then the interval must be manually switched to the next 5 minute period. The reason for this is because the speed at which we are watching a video will vary greatly, but will almost always be faster than real time, so the software does not know when you have reached the end of an interval, and therefore we must tell it when we want to switch.

An ArrayList of Interval objects holds the contents of each interval. Eventually a saving feature will be implemented and the data will be output into a csv file. 

