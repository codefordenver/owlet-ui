# Owlet

Generated using [Luminus](http://www.luminusweb.net/) version 2.9.11.68

## Development

### Running the web server

    lein run

### Running the front-end development server

    lein figwheel

### Running the sass watcher

    lein auto sassc once

## Prerequisites

### Installation

- Make sure you have a recent version of the Java Development Kit. If not,
  download the installer from [the Oracle downloads page](http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html).
  Select **Accept License Agreement** at the top of the panel for the
  latest version, then click the filename for your platform, _e.g._,
  _jdk-8u112-macosx-x64.dmg_. Go to your Downloads folder, open the .dmg file,
  and follow the instructions. To verify, enter `java -version` in a terminal.
  You should see something like the following:

      java version "1.8.0_112"
      Java(TM) SE Runtime Environment (build 1.8.0_112-b16)
      Java HotSpot(TM) 64-Bit Server VM (build 25.112-b16, mixed mode)

  Note how the version — in this case "112" — matches the file you
  downloaded.

  You'll also need the command-line utilities [`git`](https://git-scm.com),
  [`lein`](https://leiningen.org), and [`rlwrap`](https://github.com/hanslub42/rlwrap)
  installed on your system. On a Mac, the easiest way to get them is to first
  install [Homebrew](http://brew.sh), then in a terminal command line,
  execute this:

      brew install git leiningen rlwrap

  Finally, the SASSC compiler

      brew install sassc

### Clone the repo

- Now, to copy the Owlet code onto your machine, first `cd` to where you want
  the Owlet directory to be, then [clone](https://help.github.com/articles/cloning-a-repository/)
  this repository:

      git clone https://github.com/codefordenver/owlet-ui.git

- Now if you do `ls`, you should see a new directory, `owlet-ui`. Go there:

      cd owlet-ui

## Extra development tools

#### Figwheel ClojureScript REPL in Cursive/IntelliJ IDEA

To work on this project, all you really need is a terminal, a browser, and any
text editor. However, there is much to be said for using the Cursive plugin in
[JetBrain's IntelliJ IDEA](https://www.jetbrains.com/idea/).  Since it
understands how the different parts of your code and its dependencies connect
together, you can do code refactoring, smart code completion, one-click
navigation to a variable definition, instantly view docstrings, and catch arity
and spelling errors. Best of all, out of the box you can run a
**R**ead-**E**val-**P**rint-**L**oop (REPL) that knows about your code.
Quickly experiment with your live, running code in the REPL's command line,
load a small change into the REPL, switch namespaces, or run unit tests — all
with just a couple keystrokes.

To get started with IntelliJ IDEA and Cursive, follow the
[Cursive installation instructions](https://cursive-ide.com/userguide/).
Then [Import an existing Leiningen project](https://cursive-ide.com/userguide/leiningen.html),
namely owlet-ui.

To configure the Cursive REPL connected to our running app, we need to create a
[Run/Debug Configuration](https://www.jetbrains.com/help/idea/2016.1/run-debug-configuration.html).

- Open IntelliJ and select menu item **Run -> Edit Configurations...**

- Click the **+** button at the top left and choose **Clojure REPL**.

- Choose **Remote**.

- Enter a name in the **Name** field (e.g., `Owlet nRepl`).

- Choose the radio button **Use Leiningen REPL port**.

- Uncheck the box **Activate tool window** at the bottom.

- Click the **OK** button to save your REPL config.

Now, assuming you have a Clojure nREPL started in the terminal
(see [Running the application](#running-the-application), above),
you can hook in to it from Cursive at any time:

- Go to **Run -> Run...**, then select your REPL config
  (called "Owlet nRepl" above). A new Cursive REPL tool window will appear.
  You should immediately see just this in the REPL window:

      Connecting to remote nREPL server...
      Clojure 1.8.0

- Now that we're connected to the Clojure nREPL, create a ClojureScript REPL
  by evaluating the following Clojure code in the text box at the bottom of
  the REPL tool window:

      (figwheel-sidecar.repl-api/cljs-repl)

  You should see something like this output:

      ...
      Prompt will show when Figwheel connects to your application
      To quit, type: :cljs/quit
      => nil

Now, since we're just "jacking in" to the same Figwheel server, when you
modify and save a .cljs file, Figwheel will notice and automatically reload
it. From the REPL, you can control the app as it is running, since you're
evaluating code in the context of the live app. Plus, you have access to the
[Cursive REPL tools that interact with the editor](https://cursive-ide.com/userguide/repl.html#interaction),
such as:

- Switch REPL NS to current file

- Load file in REPL

- Send form before caret to REPL

- Run tests in current NS in REPL

- Add new REPL Command

<a name="cursive-repl-command"></a>
For example, a good idea would be to add your own REPL command to evaluate the
`cljs-repl` code, above. Once you have a REPL window, select **Tools -> REPL
-> Add New REPL Command**. Give your command a name, select the **Execute**
radio button, and enter `(figwheel-sidecar.repl-api/cljs-repl)` as above.
Select the **Project specific** checkbox and **OK**. Then for easy access, you
can define a keyboard shortcut of your choosing in **IntelliJ IDEA ->
Preferences... -> Keymap**.

#### More ClojureScript REPLs

Once you've run `script/figwheel-repl.sh`, a Clojure nREPL is running, and you
can "jack in" to get another Clojure REPL, then another ClojureScript REPL,
similar to [how we did it in Cursive](#figwheel-clojurescript-repl-in-cursiveintellij-idea):

- Confirm that your [nREPL started by `script/figwheel-repl.sh`](#running-the-application)
  is still running.

- From a terminal, run the following command:

      lein repl :connect

  You should now have a Clojure REPL with prompt, `owlet-ui.server=>`.

- As with Cursive,
  enter the following Clojure code at the prompt:

      (figwheel-sidecar.repl-api/cljs-repl)

  You should see output like this:

      ...
      To quit, type: :cljs/quit
      nil
      cljs.user=>


#### Debugging with Dirac

With a little extra setup, you can work on Owlet using the amazing
[Dirac DevTools](https://github.com/binaryage/dirac) browser debugging
environment. You will still be running the app with Figwheel, so modified
files will still compile and load automatically, but the browser REPL will be
running in Dirac. The Dirac environment on the browser is actually a Chrome
extension consisting of a customized fork of Chrome DevTools, the JavaScript
debugging tool built into Chrome. However, it makes use of features only
provided by the latest version of Chrome DevTools, which is why the _Canary_
version of Chrome is required.

##### Dirac installation

- If the `script/figwheel-repl.sh` process started above is running, then stop
  it (Control-d).

- Download and install the desktop application, [Google Chrome Canary](https://www.google.com/chrome/browser/canary.html).

- If you opened it, quit Chrome Canary.

- In the terminal, make sure the current working directory is still the one
  containing this README.md file.

- At the command line, run

      script/start-chrome-canary.sh

  You'll see an empty Chrome window with location http://localhost:4000/. It
  is empty because we haven't started up Owlet server yet.
  > By the way, this command is how you'll need to start up the browser whenever
  > you work on Owlet with Dirac. See [below](#using-dirac).

- Install the [Dirac DevTools extension](https://chrome.google.com/webstore/detail/dirac-devtools/kbkdngfljkchidcjpnfcgcokkbhlkogi),
  granting it access to your data. You should see a little green icon to the
  right of the address bar in the window.
  > Since you started Chrome Canary with the script above, the extension will
  > actually be saved in directory `.dirac-chrome-profile/`, so installing it
  > or changing some settings will not affect (nor be affected by) any existing
  > settings or extensions you may have in Chrome when started normally, say by
  > double-clicking the Chrome or Chrome Canary icon.

##### Using Dirac

Now that Chrome Canary and the Dirac DevTools extension are installed locally
in the Owlet project directory, let's use it with Owlet.

- In the terminal, make sure the current working directory is still the one
  containing this README.md file.

- As above, start the app with Figwheel, but this time using the `--dirac`
  option:

      script/figwheel-repl.sh dirac

  When you see the following, the nREPL has started and the Dirac server is
  waiting for the browser client:

      ...
      owlet-ui.server=>
      Dirac Agent v0.8.8
      Connected to nREPL server at nrepl://localhost:8230.
      Agent is accepting connections at ws://localhost:8231.

- If Chrome Canary isn't already running, start it by running the following
  in a separate terminal window:

      script/start-chrome-canary.sh

  You should now see the Owlet app running in the window that pops up.

  > Once you start up Chrome Canary in this way, you can leave it open, even
  > if you restart the Owlet app and the REPL.  As always, you can cleanly
  > reload the app with **View -> Force Reload This Page** (Command-Shift-R).

- Click the Dirac DevTools toolbar icon. The Dirac DevTools Console window
  should appear. Note the instructions there about switching between
  ClojureScript and JavaScript REPLs (Control-,). If you see the error
  message, "CLJS DevTools: some custom formatters were not rendered", then
  just do **View -> Force Reload This Page** (Command-Shift-R).

  > Though you may be in the habit of typing Command-Option-i, don't!
  > Do **not** open the regular Chrome DevTools.

- Try out the nice REPL in the **Console** tab and see how parentheses are
  automatically balanced, arrow keys take you up and down in the REPL history,
  symbols are completed as you type, output is colorized EDN data (not obscure
  JS objects), data structures are presented as collapsible widgets to neatly
  save space, and more!

- Try out the debugger too. it works just like the Chrome Devtools debugger,
  except that source code is both ClojureScript and the JavaScript it compiles
  to. In the **Sources** tab, drill down to **top -> localhost:4000 ->
  js/compiled -> out**, click on an Owlet .cljs file of interest, then set a
  breakpoint that will be hit when you do something in the app's GUI.  When the
  app stops at the breakpoint, look at current variables in the **Scope**
  section of the debugger. Then back in the **Console** tab, enter
  ClojureScript forms into the REPL. They will be evaluated in the breakpoint's
  context.  Click the resume button or key F8 to let the app continue.

#### Dirac ClojureScript REPL in Cursive/IntelliJ IDEA

With Dirac, you don't have to give up Cursive. Just as we connected with the
Figwheel CLJS REPL, [above](#figwheel-clojurescript-repl-in-cursiveintellij-idea),
we can connect with the Dirac REPL.

- If you have a REPL running in Cursive, stop it by clicking the X in its
  toolbar.

- Go to **Run -> Run...** and select the REPL config we created [above](#figwheel-clojurescript-repl-in-cursiveintellij-idea),
  As before, you should immediately see just this in the window:

      Connecting to remote nREPL server...
      Clojure 1.8.0

- Now, as before, we're connected to the Clojure nREPL, but this time we'll
  connect to the _Dirac_ ClojureScript REPL.  Evaluate the following Clojure
  code in the text box at the bottom of the REPL tool window:

      (dirac! :join)

  You should see something like this output:

      ...
      Your current nREPL session is a joined Dirac session (ClojureScript) which targets 'the most recent Dirac session'
      ...
      To quit, type: :cljs/quit
      => nil

[As mentioned above](#cursive-repl-command), it's a good idea to **Add New
REPL Command** and define a keyboard shortcut to type the `(dirac! :join)`
command for you.

#### More Dirac ClojureScript REPLs

You can connect with the Dirac REPL, just like we did with the Figwheel REPL,
with only a small difference. Of course, first ensure the process you started
with `script/figwheel-repl.sh dirac` is still running, then just follow [the
directions above](#more-clojurescript-repls), until the last step. Instead, do
this one:

- As with Cursive,
  enter the following Clojure code at the prompt:

      (dirac! :join)

  You should see output like this:

      ...
      To quit, type: :cljs/quit
      nil
      cljs.user=>

#### Dirac REPL Caveat

When you evaluate an expression in the Dirac ClojureScript REPL, the result
will be shown after `=>` in the terminal or Cursive REPL window, as expected.
However, side effects like printed output or exception stack traces will be
shown _only in the Dirac DevTools console_.  This can be confusing, especially
if you've inserted a print statement and you see nothing, or you don't realize
something broke because you don't see an exception! You need to look in the
Dirac DevTools console. The console will mirror the expression you entered,
its result, _and_ any printed side effects.  So just keep Chrome Canary nearby
and the Dirac DevTools window handy.

## License
The ISC License

Copyright (c) Code for Denver and Contributors

Permission to use, copy, modify, and/or distribute this software for any
purpose with or without fee is hereby granted, provided that the above
copyright notice and this permission notice appear in all copies.

THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL WARRANTIES
WITH REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED WARRANTIES OF
MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR
ANY SPECIAL, DIRECT, INDIRECT, OR CONSEQUENTIAL DAMAGES OR ANY DAMAGES
WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR PROFITS, WHETHER IN AN
ACTION OF CONTRACT, NEGLIGENCE OR OTHER TORTIOUS ACTION, ARISING OUT OF OR
IN CONNECTION WITH THE USE OR PERFORMANCE OF THIS SOFTWARE.
