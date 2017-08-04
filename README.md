# owlet

generated using Luminus version "2.9.11.68"


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

- Now, to copy the Owlet UI code onto your machine, first `cd` to where you want
  the Owlet UI directory to be, then [clone](https://help.github.com/articles/cloning-a-repository/)
  this repository:

      git clone https://github.com/codefordenver/owlet-ui.git

- Now if you do `ls`, you should see a new directory, `owlet-ui`. Go there:

      cd owlet-ui

## Running the web server

To start a web server for the application, run:

    lein run

## Running the front-end development server

    lein figwheel

## Running the sass watcher

    lein auto sassc once

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
