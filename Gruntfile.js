module.exports = function(grunt) {
	"use strict";
	// Project configuration.
	grunt.initConfig({
		pkg: grunt.file.readJSON('package.json'),
		clean: ['<%= pkg.build.path %>'],
		concat: {
			options: {
				// define a string to put between each file in the concatenated output
				separator: ';'
			},
			dist: {
				// the files to concatenate
				src: ['<%= pkg.src %>'],
				// the location of the resulting JS file
				dest: '<%= pkg.build.path + pkg.build.file %>'
			}
		},
		jshint: {
			"jshintrc":".jshintrc"
		},
		copy:{
			main:{
				files:[
					{src: ['<%= concat.dist.dest %>'], dest: '<%= pkg.dist.path + pkg.dist.file %>-<%= pkg.version %>.min.js', filter: 'isFile'}
				]
			}
		}
	});
	grunt.loadNpmTasks('grunt-contrib-clean');
	grunt.loadNpmTasks('grunt-contrib-concat');
	grunt.loadNpmTasks('grunt-contrib-copy');
	grunt.loadNpmTasks('grunt-contrib-jshint');

	// Default task(s).
	grunt.registerTask('default', ['jshint']);
	grunt.registerTask('build', ['clean', 'concat']);
	grunt.registerTask('test', ['jshint']);
	grunt.registerTask('dist', ['clean', 'concat', 'copy']);

};
