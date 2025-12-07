<script lang="ts">
  import type { Visit } from "../../types/visit.type";
  import Text from "../../components/Text";
  import TextArea from "../../components/TextArea";

  interface Props {
    visit: Visit;
  }

  let { visit }: Props = $props();

  let newVisitPetName = $state("");
  let newVisitDate = $state("");
  let newVisitText = $state("");
  let newVisitVetName = $state("");
  $effect(() => {
    newVisitPetName = visit.petItem!.text;
    newVisitDate = visit.date!;
    newVisitText = visit.text!;
    newVisitVetName = visit.vetItem!.text;
  });
</script>

<div class="flex flex-col border rounded-md p-2">
  <div class="flex flex-row gap-1">
    <div class="w-full">
      <Text value={newVisitPetName} label="Pet" />
    </div>
    <div class="w-48">
      <Text value={newVisitDate} label="Treatment" />
    </div>
  </div>
  <div class="full">
    <Text value={newVisitVetName} label="Vet" />
  </div>
  <div class="w-full">
    <TextArea value={newVisitText} disabled label="Diagnosis" />
  </div>
</div>
