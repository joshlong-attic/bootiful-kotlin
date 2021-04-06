#!/usr/bin/env kotlinc -script

import java.io.File

File(".").listFiles { file -> !file.isDirectory }!!.forEach { folder -> println(folder) }
