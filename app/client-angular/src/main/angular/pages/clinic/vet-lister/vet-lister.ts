import {
  Component,
  DestroyRef,
  OnInit,
  computed,
  inject,
  signal,
} from "@angular/core";
import { CommonModule } from "@angular/common";
import {
  FormControl,
  FormGroup,
  ReactiveFormsModule,
  Validators,
} from "@angular/forms";
import { RouterLink } from "@angular/router";
import { forkJoin } from "rxjs";
import { Toast } from "../../../controls/toast/toast";
import { EnumService } from "../../../services/enum.service";
import { VetService } from "../../../services/vet.service";
import { type EnumItem } from "../../../types/enum.type";
import { type Vet } from "../../../types/vet.type";
import { VetEditorComponent } from "../vet-editor/vet-editor";

@Component({
  selector: "app-vet-lister",
  imports: [CommonModule, RouterLink, ReactiveFormsModule, VetEditorComponent],
  templateUrl: "./vet-lister.html",
  styles: ``,
})
export class VetListerComponent implements OnInit {
  private destroyRef = inject(DestroyRef);
  private toast = inject(Toast);
  private enumService = inject(EnumService);
  private vetService = inject(VetService);
  loading = signal(false);

  filterForm = new FormGroup({
    criteria: new FormControl("", Validators.required),
  });

  allVet = signal<Vet[]>([]);
  afterCreateVet(newVet: Vet) {
    this.allVet.update((allVet) => {
      return [newVet, ...allVet];
    });
  }
  afterUpdateVet(newVet: Vet) {
    this.allVet.update((allVet) => {
      return allVet.map((vet) => (vet.id === newVet.id ? newVet : vet));
    });
  }
  afterRemoveVet(newVet: Vet) {
    this.allVet.update((allVet) => {
      return allVet.filter((vet) => vet.id !== newVet.id);
    });
  }

  newVet = computed<Vet>(() => {
    return {
      version: 0,
      name: "",
      allSkill: [],
    };
  });

  allSkillEnum = signal<EnumItem[]>([]);
  ngOnInit() {
    this.loading.set(true);
    const search = { sort: "name,asc" };
    const subscription = forkJoin({
      allSkillEnum: this.enumService.loadAllEnum("skill"),
      allVet: this.vetService.loadAllVet(search),
    }).subscribe({
      next: (value) => {
        this.allSkillEnum.set(value.allSkillEnum);
        this.allVet.set(value.allVet);
      },
      complete: () => {
        this.loading.set(false);
      },
      error: (err) => {
        this.toast.push(err);
      },
    });
    this.destroyRef.onDestroy(() => {
      subscription.unsubscribe();
    });
  }

  onFilterClicked() {
    this.loading.set(true);
    const search = { sort: "name,asc", name: this.filterForm.value.criteria! };
    const subscription = this.vetService.loadAllVet(search).subscribe({
      next: (allVet) => {
        this.allVet.set(allVet);
      },
      complete: () => {
        this.loading.set(false);
      },
      error: (err) => {
        this.toast.push(err);
      },
    });
    this.destroyRef.onDestroy(() => {
      subscription.unsubscribe();
    });
  }

  vetId = signal<string | undefined>(undefined); // no vet selected
  onVetClicked(vet: Vet) {
    this.vetId.set(vet.id);
  }

  vetEditorCreate = signal(false);
  onVetEditorCreateClicked() {
    this.vetId.set(undefined); // no vet selected
    this.vetEditorCreate.set(true);
    this.vetEditorUpdate.set(false);
  }

  vetEditorUpdate = signal(false);
  onVetEditorUpdateClicked(vet: Vet) {
    this.vetId.set(vet.id);
    this.vetEditorCreate.set(false);
    this.vetEditorUpdate.set(true);
  }

  readonly vetFilterDisabled = computed(
    () => this.vetEditorCreate() || this.vetEditorUpdate()
  );

  readonly vetEditorDisabled = computed(() => this.vetFilterDisabled());

  onVetRemoveClicked(vet: Vet) {
    this.vetId.set(undefined); // no vet selected
    const text = vet.name;
    const hint = text.length > 20 ? text.substring(0, 20) + "..." : text;
    if (!confirm("Delete vet '" + hint + "' permanently?")) return;
    this.loading.set(true);
    const subscription = this.vetService.removeVet(vet.id!).subscribe({
      next: (vet) => {
        this.afterRemoveVet(vet);
      },
      complete: () => {
        this.loading.set(false);
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
