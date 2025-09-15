<script>
  import Text from "../components/Text";
  import TextArea from "../components/TextArea";

  let { visit } = $props();

  let newVisitPetName = $derived(visit.petItem?.text);
  let newVisitDate = $derived(visit.date);
  let newVisitText = $derived(visit.text);
  let newVisitVetName = $derived(visit.vetItem?.text);
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
