# ${projectName}

## Deployment
This project leverages [Helmfile](https://helmfile.readthedocs.io/en/latest/) for deployment. To deploy the project 
locally run `helmfile apply`. This will deploy all applications except the pipeline. Once they are up, you can 
deploy the pipeline(s) with `helmfile apply --state-values-set pipeline=<pipeline name>`. To tear down everything, 
run `helmfile destroy --state-values-set pipeline=<pipeline name>`.

Running the Project in a higher environment like CI can be done by changing the `--environment` option. First the 
helmfile's `kubeContext` needs to be updated to point to the appropriate Kubernetes context. Then running the same 
helmfile apply and destroy commands above will deploy the app