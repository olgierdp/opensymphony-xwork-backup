Thanks for trying out XWork: the foundation for WebWork, Java's most 
cutting edge web development platform. WebWork is brought to you by
the OpenSymphony team. You can find out more about WebWork and
OpenSymphony at http://www.opensymphony.com.

=== Documentation ===
The documentation can be found in HTML and PDF format in the docs
directory:

 * HTML format: docs/Documentation.html
 * PDF format: docs/docs.pdf
 * Javadocs: docs/api/index.html

This documentation has been generated from the XWork wiki at the time
of this release. If you are looking for the absolute latest
documentation, please visit the wiki at:

http://wiki.opensymphony.com/display/XW/Documentation

Note that these docs could include information about features not
included in this release, so take care when referencing the wiki.

=== Building ===
If you'd like to build your own version of XWork, we've included
everything you need in this distribution. The ant script, build.xml,
contains a "jar" task that you can launch to create your own xwork
jar. The only thing you must do before hand is set up the proper jars
in the ANT_HOME/lib directory. These jars are:

 * lib/bootstrap/clover-license.jar
 * lib/build/clover.jar
 * lib/build/junit.jar

Once you've copied these three jars in to the ANT_HOME/lib directory,
simply run "ant jar" to create your own version of XWork.
