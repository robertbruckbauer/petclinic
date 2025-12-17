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
import { Toast } from "../../../controls/toast/toast";
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
  private toast = inject(Toast);
  private ownerService = inject(OwnerService);
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
    const newOwner = {
      ...this.owner(),
      name: this.form.value.name!,
      address: this.form.value.address!,
      contact: this.form.value.contact!,
    };
    if (this.owner().id) {
      const subscription = this.ownerService
        .mutateOwner(this.owner().id!, newOwner)
        .subscribe({
          next: (value) => {
            this.updateEmitter.emit(value);
            this.visible.set(false);
            this.form.reset();
          },
          error: (err) => {
            this.toast.push(err);
          },
        });
      this.destroyRef.onDestroy(() => {
        subscription.unsubscribe();
      });
    } else {
      const subscription = this.ownerService.createOwner(newOwner).subscribe({
        next: (value) => {
          this.createEmitter.emit(value);
          this.visible.set(false);
          this.form.reset();
        },
        error: (err) => {
          this.toast.push(err);
        },
      });
      this.destroyRef.onDestroy(() => {
        subscription.unsubscribe();
      });
    }
  }
}
