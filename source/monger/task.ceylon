import utils { param, todo }

doc "A task."
by "Jonthan Lovelace"
param "status" "the status of the task"
todo "Make status an enumerated type"
param "priority" "the priority of the task"
param "id" "the number of the task"
param "desc" "the description of the task"
param "dependencies" "(the numbers of) any dependencies"
todo "Make dependencies a sequenced parameter"
shared class Task(String status, Integer priority, Integer id, String desc, Sequence<Integer|Task> dependencies) { 
	doc "The status of the task"
	todo "Should be an enumerated type once the IDE supports it"
	shared String status = status;
	doc "The priority of the task ... high wins, I think, in my data"
	shared Integer priority = priority;
	doc "The number of the task."
	todo "Should this be shared?"
	Integer id = id;
	doc "The tasks this one depends on."
	SequenceBuilder<Task> deps = SequenceBuilder<Task>();
	SequenceBuilder<Integer> depsToLookUpLater = SequenceBuilder<Integer>();
	for (dep in dependencies) {
		switch (dep)
		case (is Task) {
			// Don't actually run this program until this has been implemented!
			deps.append(dep);
		} case (is Integer) {
			// TODO: Look up in a central repository later
			depsToLookUpLater.append(dep);
		} 
	}
}
