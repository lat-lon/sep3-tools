SET pljava.libjvm_location TO '/usr/lib/jvm/java-17-openjdk-amd64/lib/server/libjvm.so';
ALTER database postgres SET pljava.libjvm_location FROM current;
CREATE EXTENSION pljava;
SELECT sqlj.install_jar('file:///tmp/sep3-parser-0.0.1-SNAPSHOT-jar-with-dependencies.jar', 'sep3', true);
SELECT sqlj.set_classpath('public', 'sep3');
