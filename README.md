### 7 GUIs: A GUI Programming Benchmark 
This is my implementation of the [7 GUIs](https://eugenkiss.github.io/7guis/), written in Clojurescript and Reagent. 

#### Visit the live demo
https://seven.jonathandannel.com/.

#### Run the project locally
To start the Figwheel compiler and watch local changes, run `lein figwheel` in the project root.

Navigate to `localhost:3449` to view the running app in your browser.

### GUI-specific notes

#### Flight booker
- The error watcher will complain unless (and until) the date format is `dd/mm/yyyy`

#### Circle drawer
- Right click a circle to edit its diameter
- To undo, click the counterclockwise button icon
- To redo, click the clockwise button icon
- Clear the canvas with the trash icon

#### Spreadsheet
A formula may contain one of the following 5 operations:
   - Addition `=sum`
   - Subtraction `=sub`
   - Multiplication `=mul`
   - Division `=div`
   - Average `=avg`

Args can be passed as either ranges, individual cell coordinates, or both:
   - `=sum(b1:b4)`
   - `=sum(b1, d8)`
   - `=sum(b1:b4, d8)`

Click a formula cell to change its formula, or click a plain value cell to change its value.

### TODO
 - Display flight booker errors more gracefully and in a less annoying way
 - Spreadsheet: Display something better than `NaN` when attempting to operate on a string. This could mean changing the cell's `:computed` value directly or just rendering something else in the Reagent component
 - Polish up the UI styles a bit, and make it more responsive on mobile
