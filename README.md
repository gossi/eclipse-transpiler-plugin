# eclipse-transpiler-plugin

Eclipse plugin to automatically transpile your files (LESS, SASS, CoffeeScript, etc.).

## Installation

Install from the PEX update site: [http://p2-dev.pdt-extensions.org/](http://p2-dev.pdt-extensions.org/)<br>
You'll find the Transpiler Feature in the Toolchains category.

## Usage

### 1. Install Transpilers

1. Go to Eclipse Preferences and switch to the Transpilers page. 
2. You may want to try the Auto-Detect button which searches for transpilers on your system and adds them to eclipse
3. Add them manually by clicking the `Add` Button and fill out the dialog. Select the Transpiler at first which should prefill some fields. At least you must set the path to the executable for you transpiler (see note below).

Note:

Eclipse doesn't know about your environment (because it keeps one itself). All your `%PATH` variables, etc. won't be available in eclipse. Thus eclipse can't run a commands, such as `lessc`, `sass` or `coffee`. You need the full path to your command. See the example for the `lessc` command:

Find out your path with the `which` command:

```
$ which lessc
/usr/local/bin/lessc
```

However, in case of `lessc`, it runs on node:

```
$ head -n1 /usr/local/bin/lessc
#!/usr/bin/env node
```

Because this is an env variable itself, eclipse also doesn't know about that, so the `node` command (of course with full path) must be prepended to the `lessc` command. Search for it as above:

```
$ which node
/usr/local/bin/node
```

So, the final path to the `lessc` executable is: <br>
__/usr/local/bin/node /usr/local/bin/lessc__

So keep in mind to add the _full path_.

### 2. Configure Projects

For every project you want to use the transpilers, you must add support for them. Right click a project > Configure > Add Transpiler Support

### 3. Configure Transpilers per Project

Once Transpilers are installed, configure them per project. Open the properties for a project and switch to the Transpilers page.

1. Add the transpiler to the project you installed earlier by clicking the "Add" button and selecting one.
2. Select the transpiler in the upper area to activate the panel below.
3. Add paths for your transpiling actions. Set source and destination. Either by files directly or selecting a directory (where files are matched by the extensions you set during installation).
4. Select transpiler specific options.

## Contributing

Please feel free to open [issues](issues) for problems you are facing or [fork](fork) the repo and add your wishes.

