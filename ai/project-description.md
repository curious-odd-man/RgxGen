I have an open-source java library found here: https://github.com/curious-odd-man/RgxGen

Library is used to generate random text based on the regex pattern.

First library parses the regex expression into a compound design pattern, then uses visitor pattern to traverse Nodes
and generate values.

All nodes can be found here: `src/main/java/com/github/curiousoddman/rgxgen/nodes`