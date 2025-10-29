import {
  Component,
  DestroyRef,
  OnInit,
  inject,
  input,
  model,
  output,
} from "@angular/core";
import {
  FormControl,
  FormGroup,
  ReactiveFormsModule,
  Validators,
} from "@angular/forms";
import { OwnerService } from "../../../services/owner.service";
import { type Owner } from "../../../types/owner.type";

@Component({
  selector: "app-owner-editor",
  imports: [ReactiveFormsModule],
  templateUrl: "./owner-editor.html",
  styles: ``,
})
export class OwnerEditorComponent implements OnInit {
  private destroyRef = inject(DestroyRef);
  private restApi = inject(OwnerService);
  mode = input.required<"create" | "update">();
  visible = model.required<boolean>();
  owner = input.required<Owner>();
  form = new FormGroup({
    name: new FormControl("", Validators.required),
    address: new FormControl("", Validators.required),
    contact: new FormControl("", Validators.required),
  });

  ngOnInit() {
    this.form.patchValue(this.owner());
  }

  get isSubmittable() {
    return this.form.dirty && this.form.valid;
  }

  cancelEmitter = output<Owner>({ alias: "cancel" });
  onCancelClicked() {
    this.cancelEmitter.emit(this.owner());
    this.visible.set(false);
    this.form.reset();
  }

  createEmitter = output<Owner>({ alias: "create" });
  updateEmitter = output<Owner>({ alias: "update" });
  onSubmitClicked() {
    if (this.mode() === "create") {
      const subscription = this.restApi
        .createOwner({
          ...this.owner(),
          name: this.form.value.name!,
          address: this.form.value.address!,
          contact: this.form.value.contact!,
        })
        .subscribe({
          next: (item) => {
            this.createEmitter.emit(item);
            this.visible.set(false);
            this.form.reset();
          },
        });
      this.destroyRef.onDestroy(() => {
        subscription.unsubscribe();
      });
    } else {
      const subscription = this.restApi
        .updateOwner({
          ...this.owner(),
          name: this.form.value.name!,
          address: this.form.value.address!,
          contact: this.form.value.contact!,
        })
        .subscribe({
          next: (item) => {
            this.updateEmitter.emit(item);
            this.visible.set(false);
            this.form.reset();
          },
        });
      this.destroyRef.onDestroy(() => {
        subscription.unsubscribe();
      });
    }
  }
}
